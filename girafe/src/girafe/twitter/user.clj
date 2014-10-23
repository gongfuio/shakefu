(ns girafe.twitter.user
  (:require [quil.core :as q]
            [clojure.string :refer [blank?]])
  (:import  [toxi.physics VerletParticle]
            [toxi.geom Vec3D]))

(def RADIUS 40.0)

(defn create
  [name x y z]
  (let [location (Vec3D. x y z)]
    { :name name
      :particle (VerletParticle. location) }))

(defn display
  [{:keys [name ^VerletParticle particle] :as usr}]
  (q/push-matrix)
  (q/stroke 64 64 192)
  (q/stroke-weight 1)
  (q/fill 64 64 192 64)
  (let [x (.x particle)
        y (.y particle)
        z (.z particle)]
    ; Display a sphere to represent a user
    (q/with-translation [x y z]
      (q/sphere RADIUS))
    ; and its name next to it
    (when-not (blank? name)
      (q/text-size 24)
      (q/text-align :left :center)
      (q/text name (+ x RADIUS) y (* 2 RADIUS))))
  (q/pop-matrix))
