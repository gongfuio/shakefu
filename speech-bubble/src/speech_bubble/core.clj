(ns speech-bubble.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[speech-bubble.text-sprites :as bubble]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)

  (let [text "learning clojure is fun"
		font (q/create-font "consolas" 18)  ; font-specified must be system available or in data folder
		font-size 30
		texture-size [200 600]]
  	{:speech-sprite (bubble/speech-sprite text font font-size texture-size)}))

(defn update [state]
  state)

(defn draw
  [{speech-sprite :speech-sprite}]
  (q/background 0)
  (q/with-translation [100 200]
  	(bubble/draw-speech-sprite speech-sprite)))

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
