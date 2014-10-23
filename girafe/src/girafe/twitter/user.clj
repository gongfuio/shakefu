(ns girafe.twitter.user
  (:require [quil.core :as q]
            [clojure.string :refer [blank?]])
  (:import  [toxi.geom Vec3D]))

(def RADIUS 40.0)

(defn create
  [name x y z]
  { :name name
    :location (Vec3D. x y z) })

(defn display!
  [{:keys [name ^Vec3D location] :as usr}]
  (q/push-matrix)
  (q/stroke 64 64 192)
  (q/stroke-weight 1)
  (q/fill 64 64 192 64)
  (let [x (.x location)
        y (.y location)
        z (.z location)]
    ; Display a sphere to represent a user
    (q/with-translation [x y z]
      (q/sphere RADIUS))
    ; and its name next to it
    (when-not (blank? name)
      (q/text-size 24)
      (q/text-align :left :center)
      (q/text name (+ x RADIUS) y (* 2 RADIUS))))
  (q/pop-matrix))
