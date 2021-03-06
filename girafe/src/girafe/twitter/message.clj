(ns girafe.twitter.message
  "Functions to display speech bubbles containing the text content
  of tweets. The source hereafter is a copy of David's speech-bubble
  subproject (speech-bubble/src/speech-bubble/text_sprites.clj)."
	(:require [quil.core :as q])
  (:import  [toxi.physics VerletParticle]
            [toxi.geom Vec3D]))

(defn text-sprite
  "creates a text sprite (PGraphics)
   text is rendered in the box (width/height)
   this function does not compute text height (must be guessed)"
  [text font font-size [width height]]
   (let [sprite (q/create-graphics width height)]
	 (q/with-graphics sprite
	   (q/text-font font)
	   (q/text-size font-size)
	   (q/text-leading font-size) 	; spacing between lines
	   (q/fill 255) 			 	; text-color
	   (q/text text 0 0 width height))
	 sprite))

(defn text-height
  "computes text height (pixels) given a PGraphics (sprite)
  in which text has been written"
  [sprite]
  (let [pixels-array (q/pixels sprite)
		pixel-count (count pixels-array)
		sprite-width (.width sprite)
		extract-alpha #(bit-and (bit-shift-right % 24) 0xff)
		divide-by #(/ %2 %) ; flip arguments for arrow macro
		alphas (map extract-alpha (reverse pixels-array))]
	(->> alphas
	 (take-while #(< % 28))
	 count
	 (- pixel-count)
	 (divide-by sprite-width)
	 int)))

;; Alternative possibility with the following; we need to estimate the number
;; of text line drawn by (q/text ...) in that case, to get the text-height
;;
;; (defn line-height
;;   "Returns the line height of the current font at its current size."
;;   []
;;   (+ (q/text-ascent) (q/text-descent)))

(defn speech-sprite
  "return speech sprite map
  which can be rendered with draw-speech-sprite
  !! bubble-height is speculative (must be guestimated higher than actual rendered text)"
  [text font font-size [bubble-width bubble-height :as texture-size]]
  (let [text-margin 20
		tail-size 20 ; height in pixels of bubble tail
		text-width (- bubble-width (* text-margin 2))
		text-sprite (text-sprite text font font-size [text-width bubble-height])
		text-height (text-height text-sprite)]
  {:bubble-width bubble-width
   :bubble-height (+ text-height (* text-margin 2) tail-size)
   :text-margin 20
   :tail-size tail-size
   :text-sprite text-sprite
   }))

(defn create
  "Given a PFont, font size and a text, calculate and return a map with the bubble
  size and text metrics that can be drawn with `draw-speech-sprite`. Sample usage:

      (message/create \"Learning Clojure is fun\"
                      (q/create-font \"consolas\" 18) 30)"
  [text font font-size x y z]
  (let [location (Vec3D. x y z)
        texture-size [200 600]]
  	{ :particle (VerletParticle. location)
      :sprite (speech-sprite text font font-size texture-size) }))

(defn half [value] (/ value 2))

(defn draw-bubble
  "draws speech bubble shape"
  [width height tail-size]
    (let [half-w (/ width 2)
          draw-height (- height tail-size)]
      (q/begin-shape)
      (q/vertex 0 0)
      (q/vertex width 0)
      (q/vertex width draw-height)
      (q/vertex (+ half-w tail-size) draw-height)
      (q/vertex half-w (+ draw-height tail-size))
      (q/vertex (- half-w tail-size) draw-height)
      (q/vertex 0 draw-height)
      (q/end-shape :close)))

(defn draw-speech-sprite
  "draws a speech-sprite map"
  [{:keys [sprite particle]}]
  (let [{:keys [bubble-width bubble-height text-margin text-sprite tail-size]} sprite
        x (.x particle)
        y (.y particle)
        z (.z particle)]
    (q/push-matrix)
    (q/scale 1)
    (q/fill 64 128 192)
    (q/no-stroke)
    (q/with-translation [x y z]
      (q/with-translation [(- (half bubble-width)) (- bubble-height)]
        (draw-bubble bubble-width bubble-height tail-size)
        (q/image text-sprite text-margin text-margin)))
    (q/pop-matrix)))
