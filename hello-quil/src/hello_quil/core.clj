(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
			[hello-quil.p]
			[hello-quil.tweet-connection :as tweet]
			[cheshire.core :refer :all]
			[overtone.at-at :as at-at]))

; pool of threads for at-at
(def thread-pool (at-at/mk-pool))


(def tweet (atom {:id_str ""
				  :text ""
				  :profile-image-url nil}))

@tweet


(defn update-tweet
  [http-body]
  (let [new-tweet (parse-string http-body keyword)
		profile-image-url #(-> % :user :profile_image_url)]
	  (if (not (= (:id_str new-tweet) (:id_str @tweet))); only update if there is a new tweet
	    (reset! tweet {:id_str (:id_str new-tweet)
					   :text (:text new-tweet)
					   :profile-image-url (profile-image-url new-tweet)}))))

(def connection (tweet/connect-tweet-server thread-pool 4000 update-tweet))

;(tweet/stop-connection connection)

(defn image-available?
  "checks if requested PImage is available"
  [pImage]
  (and (not (nil? pImage)) (> (.height pImage) 0)))

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
		new-tweet? (not (= old_id_str new_id_str))]
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)
   :old_id_str (if new-tweet? new_id_str old_id_str)
   :text (:text @tweet)
   :profile-image (if new-tweet? (-> @tweet :profile-image-url q/request-image) (:profile-image state))}))

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
  ; draw text from tweet
  (when-let [text (:text state)] (q/text text 50 0 200 800))
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
