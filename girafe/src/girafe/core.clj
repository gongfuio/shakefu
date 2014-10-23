(ns girafe.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [girafe.twitter.user :as twitter-user]
            [girafe.physics :as physics])
  (:import  [toxi.physics VerletPhysics]))

(defn setup []
  (q/frame-rate 24)
  (q/color-mode :rgb)
  (q/sphere-detail 15)
  (q/smooth)
  (q/lights)

  ; Setup function returns initial state
  (let [world (physics/create-world 500.0)
        user1 (twitter-user/create "User 1"  50.0 -50.0    0.0)
        user2 (twitter-user/create "User 2"   0.0   0.0   50.0)
        user3 (twitter-user/create "User 3" -50.0  50.0  100.0) ]
    { :color 0
      :angle 0
      :physics world
      :users [ user1 user2 user3 ] }))

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
