(ns girafe.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [girafe.twitter.user :as twitter-user]
            [girafe.twitter.message :as twitter-msg]
            [girafe.physics :as physics])
  (:import  [toxi.physics VerletPhysics]))

(defn setup []
  (q/frame-rate 24)
  (q/color-mode :rgb)
  (q/sphere-detail 15)
  (q/smooth)
  (q/lights)

  ; Setup function returns initial state
  (let [font    (q/create-font "Consolas" 18)  ; font-specified must be system available or in data folder
        user1   (twitter-user/create "User 1"  200.0 -200.0    0.0)
        user2   (twitter-user/create "User 2"    0.0    0.0 -200.0)
        user3   (twitter-user/create "User 3" -200.0  200.0  250.0)
        bubble1 (twitter-msg/create "Learning Clojure is fun" font 18 200.0 -200.0 0.0)
        part1   (:particle user1)
        part2   (:particle user2)
        part3   (:particle user3)
        part4   (:particle bubble1)
        world   (physics/create-world 500.0)]
    { :color 0
      :angle 0
      :physics (-> world
                   ; (physics/add-gravity)
                   (physics/add-particle part1)
                   (physics/add-particle part2)
                   (physics/add-particle part3)
                   (physics/add-particle part4)
                   (physics/add-spring   part1 part2 150.0 0.0001)
                   (physics/add-spring   part1 part3 150.0 0.0001)
                   (physics/add-spring   part2 part3 150.0 0.0001)
                   (physics/add-spring   part1 part4  40.0 0.0200))
      :users [ user1 user2 user3 ]
      :messages [ bubble1 ]}))

(defn update [state]
  ; Within the physics engine, update all forces and particle positions accordingly
  (physics/update (:physics state))
  ; Update sketch state (obsolete, kept as an example of updating the state)
  (assoc state
    :color (mod (+ (:color state) 0.7) 255)
    :angle (+ (:angle state) 0.1)))

(defn draw [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255)
  ; Display the Twitter users
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (doseq [msg (:messages state)]
  	  (twitter-msg/draw-speech-sprite msg))
    (doseq [usr (:users state)]
      (twitter-user/display usr))))

(q/defsketch girafe
  :title "Soft-Shake Tweets"
  :size [600 600]
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
