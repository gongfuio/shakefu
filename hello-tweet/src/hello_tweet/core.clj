(ns hello-tweet.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[hello-tweet.tweet-connection :as tweet]
			[overtone.at-at :as at-at]))

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
(def connection (tweet/connect-tweet-server thread-pool 4000 update-tweet))

; for live coding and testing
;(tweet/stop-connection connection)

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
   :profile-image nil})

(defn update [state]
  ; Update state map
  ; if @tweet has been updated inject new fields in state map
  ; and request new profile image
  (let [state_id_str (:old_id_str state)
		tweet_id_str (:id_str @tweet)
		new-tweet? (not (= state_id_str tweet_id_str))]
  	{:old_id_str (if new-tweet? tweet_id_str state_id_str)
   	 :text (:text @tweet)
   	 :screen-name (:screen-name @tweet)
   	 :profile-image (if new-tweet? (-> @tweet :profile-image-url q/request-image) (:profile-image state))}))

(defn draw [{:keys [:text :screen-name :profile-image] :as state}]
  ; Clear the sketch by filling it with black
  (q/background 0)
  ; set fill color (text color)
  (q/fill 230)
  ; draw text
  (if text
	(do
		(q/text text 60 0 200 800)
		(q/text (str \@ screen-name) 280 10)))
  ; draw profile-image if available
  (if (image-available? profile-image) (q/image profile-image 0 0)))

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
