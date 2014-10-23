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


(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {})

(defn update [state]
  ; Update sketch state by changing circle color and position.
  state)


(defn draw [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  ; Set circle color.
  (q/fill 255)
  (draw-bubble 45 10 150 100)
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
