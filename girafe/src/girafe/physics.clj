(ns girafe.physics
  (:import  [toxi.physics VerletPhysics]
            [toxi.geom AABB]))

(def DEFAULT-BOUNDS 400.0)

(defn ^VerletPhysics create-world
  ([]
    (create-world DEFAULT-BOUNDS))
  ([diam]
    (.setWorldBounds (VerletPhysics.)
                     (AABB. (float diam)))))
