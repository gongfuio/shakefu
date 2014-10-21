(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[hello-quil.p]
			[hello-quil.tweet-connection :as tweet]
			[overtone.at-at :as at-at]))

; pool of threads for at-at
(def thread-pool (at-at/mk-pool))


(def tweet (atom {:id_str ""
				  :text ""
				  :screen-name ""
				  :profile-image-url nil}))


(defn update-tweet
  [new-tweet]
	  (if (not (= (:id_str new-tweet) (:id_str @tweet))); only update if there is a new tweet
	    (reset! tweet {:id_str (:id_str new-tweet)
					   :text (:text new-tweet)
					   :screen-name (get-in new-tweet [:user :screen_name])
					   :profile-image-url (get-in new-tweet [:user :profile_image_url])})))

(def connection (tweet/connect-tweet-server thread-pool 4000 update-tweet))

(tweet/stop-connection connection)

(defn image-available?
  "checks if requested PImage is available"
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
  ; Update sketch state by changing circle color and position.
  (let [old_id_str (:old_id_str state)
		new_id_str (:id_str @tweet)
		new-tweet? (not (= old_id_str new_id_str))]
  {:old_id_str (if new-tweet? new_id_str old_id_str)
   :text (:text @tweet)
   :screen-name (:screen-name @tweet)
   :profile-image (if new-tweet? (-> @tweet :profile-image-url q/request-image) (:profile-image state))}))

(defn draw [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  ; draw text
  (q/fill 230)
  (when-let [text (:text state)]
	(q/text text 60 0 200 800)
	(q/text (str \@ (:screen-name state)) 280 10))
  ; draw profile-image
  (let [profile-image (:profile-image state)]
    (if (image-available? profile-image) (q/image profile-image 0 0))))

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
