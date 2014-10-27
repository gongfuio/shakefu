(ns hello-tweet.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [hello-tweet.tweet-connection :as tweet]
            [overtone.at-at :as at-at]
            [clojure.core.async :refer [<!! alts!! >! chan go]]))

(defn img-loaded? [image]
  (and image
       (> (.-width image) 0)))

(let [tweets (chan 1)]

  (defn- tweet->state [tweet]
    {:text (get tweet :text "")
     :profile-name (str \@ (get-in tweet [:user :screen_name] ""))
     :profile-image-url (get-in tweet [:user :profile_image_url] "")
     :profile-image nil})

  (defn- start-connection []
    (let [thread-pool (at-at/mk-pool)
          update-tweet (fn [new-tweet] (go (>! tweets new-tweet)))]
      (tweet/connect-tweet-server thread-pool 4000 update-tweet)))

  (defn- update[state]
    (let [[tweet source] (alts!! [tweets] :default nil)]
      (if tweet
        (let [new-state (tweet->state tweet)
              profile-image (q/request-image (:profile-image-url new-state))]
          (conj new-state {:profile-image profile-image}))
        state)))

  (defn- draw [state]
    (let [text (:text state)
          profile-name (:profile-name state)
          profile-image (:profile-image state)]
    ; Clear the sketch by filling it with black
    (q/background 0)
    ; set fill color (text color)
    (q/fill 230)
    ; draw text
    (do
      (q/text text 60 0 200 800)
      (q/text profile-name 280 10)
      (when (img-loaded? profile-image) (q/image profile-image 0 0))
      )))

  (defn setup []
    ; Set frame rate to 30 frames per second.
    (q/frame-rate 30)
    ; Set color mode to HSB (HSV) instead of default RGB.
    (q/color-mode :hsb)
    (start-connection)
    (tweet->state (<!! tweets))))

(q/defsketch hello-quil
  :title "tweet display hello world"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update is called on each iteration before draw is called.
  ; It updates sketch state.
  :update update
  :draw draw
  ; We want Quil to provide us state when calling update/draw
  :middleware [m/fun-mode])
