(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[hello-quil.p]
			[hello-quil.tweet-connection :as tweet]
			[cheshire.core :refer :all]
			[overtone.at-at :as at-at]))

; pool of threads for at-at
(def thread-pool (at-at/mk-pool))

; tweets is a sliding queue of tweets
(def tweets (atom []))

; setup connection to tweets
(defn tweet-handler
  [http-body]
  (let [tweet (parse-string http-body keyword)
		max-queue-size 4]
   (if (not (contains? @tweets tweet))
	 (do
	 	(swap! tweets conj tweet)
	   	(if (> (count @tweets) max-queue-size) (swap! tweets subvec 1))))))

(def connection (tweet/connect-tweet-server thread-pool 4000 tweet-handler))

;(tweet/stop-connection connection)


; setup state for the current tweet to be displayed
;
(def tweet (atom {:id_str ""
				  :text ""
				  :profile-image-url nil}))


(defn update-tweet
  []
  (let [latest-tweet (last @tweets)
		profile-image-url #(-> % :user :profile_image_url)]
	(if (not (= (:id_str latest-tweet) (:id_str @tweet)))
	  (reset! tweet {
					 :id_str (:id_str latest-tweet)
					 :text (:text latest-tweet)
					 :profile-image-url (profile-image-url latest-tweet)
					}))))

(def update-tweet-schedule (at-at/every 8000 update-tweet thread-pool))

;(at-at/stop update-tweet-schedule)

@tweet

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0
   :old_id_str ""
   :profile-image nil})

(defn update [state]
  ; Update sketch state by changing circle color and position.
  (let [old_id_str (:old_id_str state)
		new_id_str (:id_str @tweet)
		should-update-tweet (not (= old_id_str new_id_str))]
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)
   :old_id_str (if should-update-tweet new_id_str old_id_str)
   :text (:text @tweet)
   :profile-image (if should-update-tweet (-> @tweet :profile-image-url q/load-image) (:profile-image state))}))

(defn draw [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  ; Calculate x and y coordinates of the circle.
  (let [angle (:angle state)
        x (* 150 (q/cos angle))
        y (* 150 (q/sin angle))
		half-width (/ (q/width) 2)
		half-height (/ (q/height) 2)]
    ; Move origin point to the center of the sketch.
    (q/with-translation [half-width half-height]
      ; Draw the circle.
      (q/ellipse x y 100 100)))
  (q/text (:text state) 50 0 200 800)
  (when-let [profile-image (:profile-image state)] (q/image profile-image 0 0)))

(q/defsketch hello-quil
  :title "You spin my circle right round"
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
