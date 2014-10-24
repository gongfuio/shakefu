(ns girafe.twitter.user
  (:require [quil.core :as q]
            [clojure.string :refer [blank?]])
  (:import  [toxi.physics VerletParticle]
            [toxi.geom Vec3D]))

(def HEAD-RADIUS 40.0)
(def BODY-SIZE (* HEAD-RADIUS 1.5))

(defn create
  [name x y z]
  (let [location (Vec3D. x y z)]
    { :name name
      :particle (VerletParticle. location) }))

(defn display
  [{:keys [name ^VerletParticle particle] :as usr}]
  (q/push-matrix)
  (q/stroke 255 255 255)
  (q/stroke-weight 1)
  (q/fill 64 128 192)
  (let [x (.x particle)
        y (.y particle)
        z (.z particle)]
    ; Display a box surmounted by a sphere to represent a user
    (q/with-translation [x y z]
      (q/sphere HEAD-RADIUS))
    (q/with-translation [x (+ y BODY-SIZE) z]
      (q/box (* BODY-SIZE 1.4) BODY-SIZE BODY-SIZE))
    ; and its name next to it
    (when-not (blank? name)
      (q/text-size 24)
      (q/text-align :left :center)
      (q/text name (+ x HEAD-RADIUS) y (* 2 HEAD-RADIUS))))
  (q/pop-matrix))
