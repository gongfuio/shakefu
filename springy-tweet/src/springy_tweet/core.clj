(ns springy-tweet.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[springy-tweet.tweet-connection :as tweet]
			[overtone.at-at :as at-at]
			[tween-clj.core :as t]
			[speech-bubble.text-sprites :as bubble]))

; pool of threads for at-at
(def thread-pool (at-at/mk-pool))

; mutable state holding the fields we wish to display
(def tweet (atom {:id_str ""
				  :text ""
				  :screen-name ""
				  :profile-image-url nil}))

(defn update-tweet
	"function called on reception of a new tweet.
 	new-tweet is a map of the json structure returned by twitter
 	keys as :keywords with js syntax (:screen_name instead of idiomatic :screen-name)"
  [new-tweet]
	  (if (not (= (:id_str new-tweet) (:id_str @tweet))); only update if there is a new tweet
	    (reset! tweet {:id_str (:id_str new-tweet)
					   :text (:text new-tweet)
					   :screen-name (get-in new-tweet [:user :screen_name])
					   :profile-image-url (get-in new-tweet [:user :profile_image_url])})))

; start connection
(def connection (tweet/connect-tweet-server thread-pool 6000 update-tweet))

; for live coding and testing
; (tweet/stop-connection connection)


(defn image-available?
  "checks if requested PImage is available (for use with q/request-image)"
  [pImage]
  (and (not (nil? pImage)) (> (.height pImage) 0)))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state.
  {:old_id_str ""
   :profile-image nil
   :speech-bubble nil
   :font (q/create-font "roboto" 18)
   :font-size 20})

(defn new-bubble
  "returns a map with a speech-sprite
  and info necessary to tween the bubble's scale"
  [font font-size text]
   {:speech-sprite (bubble/speech-sprite text font font-size [200 600])
	:scale 0
	:tween-fn (partial t/ease-out t/transition-elastic)
	:duration 2000
	:start-t (q/millis)})

(defn update-t
  [start end duration]
  (min (/ (- end start)
		  duration )
	   1))

(defn update-bubble
  "update tween values for a speech-bubble (in this case only scale)"
  [{:keys [start-t tween-fn duration] :as speech-bubble}]
  (let [t (update-t start-t (q/millis) duration)]
	(assoc speech-bubble :scale (tween-fn t))))

(defn update [{:keys [old_id_str profile-image speech-bubble font font-size :as state]}]
  ; Update state map
  ; if @tweet has been updated inject new fields in state map
  ; and request new profile image
  (let [state_id_str old_id_str
		tweet_id_str (:id_str @tweet)
		new-tweet? (not (= state_id_str tweet_id_str))
		; speech bubble
		speech-bubble (if new-tweet? (new-bubble font font-size (:text @tweet)) (update-bubble speech-bubble))]
  	{:old_id_str (if new-tweet? tweet_id_str state_id_str)
   	 ;:screen-name (:screen-name @tweet)
   	 :profile-image (if new-tweet?
					  (-> @tweet :profile-image-url q/request-image)
					  profile-image)
	 :speech-bubble speech-bubble
	 :font font
	 :font-size font-size}))

(defn draw [{:keys [profile-image speech-bubble] :as state}]
  ; Clear the sketch by filling it with black
  (q/background 0)
  ; draw speech bubble
  (if speech-bubble
	(q/with-translation [350 350]
		(q/push-matrix)
		(q/scale (:scale speech-bubble))
  		(bubble/draw-speech-sprite (:speech-sprite speech-bubble))
		(q/pop-matrix)
  		; draw profile-image if available
  		(if (image-available? profile-image)
		  (let [half-width (/ (.width profile-image) 2)]
		  	(q/image profile-image (- half-width) 5))))))

(q/defsketch hello-quil
  :title "tweet display hello world"
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
