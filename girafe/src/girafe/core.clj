(ns girafe.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [girafe.twitter.user :as twitter-user])
  (:import  [toxi.physics VerletPhysics]
            [toxi.geom AABB]))

(defn setup []
  (q/frame-rate 24)
  (q/color-mode :rgb)
  (q/sphere-detail 15)
  (q/smooth)
  (q/lights)

  ; Setup function returns initial state
  (let [physics (.setWorldBounds (VerletPhysics.) (AABB. 400.0))
        user1   (twitter-user/create "User 1" 10.0 10.0 0.0)
        user2   (twitter-user/create "User 2" 30.0 -30.0 10.0)]
    { :color 0
      :angle 0
      :physics physics
      :users [ user1 user2 ] }))

(defn update [state]
  ; Update sketch state by changing circle color and position.
  (assoc state
    :color (mod (+ (:color state) 0.7) 255)
    :angle (+ (:angle state) 0.1)))

(defn draw [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255)
  (q/with-translation [(/ (q/width) 2)
                       (/ (q/height) 2)]
    ; Display the Twitter users
    (doseq [usr (:users state)]
      (twitter-user/display! usr))))

(q/defsketch girafe
  :title "Soft-Shake Tweets"
  :size [500 500]
  :renderer :p3d
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update is called on each iteration before draw is called.
  ; It updates sketch state.
  :update update
  :draw draw
  ; This sketch uses functional-mode middleware. Check quil wiki
  ; for more info about middlewares and particularly fun-mode.
  :middleware [m/fun-mode])
