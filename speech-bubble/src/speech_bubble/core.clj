(ns speech-bubble.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[tween-clj.core :as t]
			[speech-bubble.text-sprites :as bubble]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  (let [text "learning clojure is fun"
		font (q/create-font "roboto" 18)
		font-size 20
		texture-size [200 600]]
  	{:speech-sprite (bubble/speech-sprite text font font-size texture-size)
	 :scale 0
	 :tween-fn (partial t/ease-out t/transition-elastic)
	 :duration 2000
	 :start-t (q/millis)
	 }))

(defn update-t
  [start end duration]
  (min (/ (- end start)
		  duration )
	   1))


(defn update [{:keys [speech-sprite scale tween-fn duration start-t :as state]}]
  (let [t (update-t start-t (q/millis) duration)]
	  { :speech-sprite speech-sprite
		:scale (tween-fn t)
		:tween-fn tween-fn
		:duration duration
		:start-t start-t
	   }))

; just for testing
(defn key-released
  [state]
  (-> state
	  (assoc :start-t (q/millis)
			 :duration 1000
			 :tween-fn (partial t/ease-out t/transition-bounce))))

(defn draw
  [{speech-sprite :speech-sprite
	scale :scale}]
  (q/background 0)
  (q/with-translation [150 200]
	(q/push-matrix)
	(q/scale scale)
  	(bubble/draw-speech-sprite speech-sprite)
	(q/pop-matrix)))

(q/defsketch speech-bubble
  :title "I daresay hello to you"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update is called on each iteration before draw is called.
  ; It updates sketch state.
  :update update
  :draw draw
  :key-released key-released
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
