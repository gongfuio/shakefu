(ns speech-bubble.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-bubble
  [x y width height]
  (q/with-translation [x y]
	(let [half-w (/ width 2)
		  half-tail 20]
  		(q/begin-shape)
  		(q/vertex 0 0)
  		(q/vertex width 0)
		(q/vertex width height)
		(q/vertex (+ half-w half-tail) height)
		(q/vertex half-w (+ height half-tail))
		(q/vertex (- half-w half-tail) height)
  		(q/vertex 0 height)
	  	(q/end-shape :close))))


(defn text-sprite
  "creates a text sprite (PGraphics)
   text is rendered in the box (width/height)
   this function does not compute text height (must be guessed)"
  [text font font-size [width height]]
   (let [sprite (q/create-graphics width height)]
	 (q/with-graphics sprite
	   (q/text-font font)
	   (q/text-size font-size)
	   (q/text-leading font-size) 	; spacing between
	   (q/fill 122) 			 	; text-color
	   (q/text text 0 0 width height))
	 sprite))

; 	// computes text height (measured in pixels)
;    // set textHeight to 0
;    // iterate backwards starting at the end of the pixel buffer
;    // at each step:
;    // 1. get alpha of pixel
;    // 2. break if alpha > 28  and set textHeight = current index / buffer.width
;    private void computeTextHeight(){
;        m_buffer.loadPixels();
;        m_textHeight = 0;
;        // start from end and find first instance of alpha == 255
;        for (int i = m_buffer.pixels.length -1; i >= 0; --i) {
;            int a = (m_buffer.pixels[i] >> 24) & 0xFF;
;
;            if (a > 28){
;                m_textHeight = i / m_buffer.width;
;                break;
;            }
;        }
;    }

(defn text-height
  "computes text height (pixels) given a PGraphics (sprite)"
  [sprite]
  (let [pixels-array (q/pixels sprite)
		pixel-count (count pixels-array)
		sprite-width (.width sprite)
		extract-alpha #(bit-and (bit-shift-right % 24) 0xff)
		alphas (map extract-alpha (reverse pixels-array))]
	(int(/ (- pixel-count (count (take-while #(< % 28) alphas))) sprite-width))))

(defn divide
  [w c]
  (/ c w))

(let [alphas [0 0 4]
	  pixel-count 7
	  sprite-width 2]
(->> alphas
	 (take-while #(< % 28))
	 count
	 (- pixel-count)
	 (divide sprite-width)
	 int))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains

  (let [text "learning clojure is fun"
		font (q/create-font "consolas" 18)  ; font-specified must be system available or in data folder
		font-size 42
		texture-size [200 600]
		texture (text-sprite text font font-size texture-size)]
  	{:texture texture
	 :text-height (text-height texture)
	 :position [100 100]
	 :text-width (first texture-size)}))

(defn update [state]
  ; Update sketch state by changing circle color and position.
  state)




(defn draw [{texture :texture text-height :text-height [x y] :position text-width :text-width }]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  ; Set circle color.
  (q/fill 255)
  (let [margin 30
		half-margin (/ margin 2)
		xm (- x half-margin)
		ym (- y half-margin)
		wm (+ text-width margin)
		hm (+ text-height margin)]
  	(draw-bubble xm ym wm hm))
  (q/fill 135)
  (println text-height)
  (q/image texture x y)
  )

(q/defsketch speech-bubble
  :title "I daresay hello to you"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update is called on each iteration before draw is called.
  ; It updates sketch state.
  :update update
  :draw draw
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
