(ns hello-tweet.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [hello-tweet.tweet-connection :as tweet]
            [overtone.at-at :as at-at]
            [clojure.core.async :refer [<!! >! chan go]]))

(defn img-loaded? [image]
  (and image
       (> (.-width image) 0)))

(let [tweets (chan)]

  (defn- start-connection []
    (let [thread-pool (at-at/mk-pool)
          update-tweet (fn [new-tweet] (go (>! tweets new-tweet)))]
      (tweet/connect-tweet-server thread-pool 4000 update-tweet)))

  (defn- draw []
    (let [tweet (<!! tweets)
          text (:text tweet)
          screen-name (get-in tweet [:user :screen_name])
          image-url (get-in tweet [:user :profile_image_url])]
      ; Clear the sketch by filling it with black
      (q/background 0)
      ; set fill color (text color)
      (q/fill 230)
      ; draw text
      (do
        (q/text text 60 0 200 800)
        (q/text (str \@ screen-name) 280 10)))))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  (start-connection))

(q/defsketch hello-quil
  :title "tweet display hello world"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  :draw draw
  )
