(ns girafe.physics
  "Helper functions to interact with the Toxiclibs Physics engine
  that uses Verlet integration."
  (:import  [toxi.physics VerletPhysics VerletParticle VerletSpring VerletMinDistanceSpring]
            [toxi.physics.behaviors GravityBehavior]
            [toxi.geom AABB Vec3D]))

(defn ^VerletPhysics create-world
  "Creates a physics world, with a bounding box of the given size
  (400 per default)."
  ([]
    (create-world 400.0))
  ([diameter]
    (.setWorldBounds (VerletPhysics.)
                     (AABB. (float diameter)))))

(defn ^VerletPhysics add-gravity
  "Adds a gravity field to the physics world."
  [^VerletPhysics physics]
  (do (.addBehavior physics
                    (GravityBehavior. (Vec3D. 0.0 0.005 0.0))))
  physics)

(defn ^VerletPhysics add-particle
  "Adds an individual particle to the physics world."
  [^VerletPhysics physics ^VerletParticle particle]
  (.addParticle physics particle))

(defn ^VerletPhysics add-spring
  "Adds a spring to the physics world, connecting two particles in space.
  The simulation takes particle weight into account."
  [^VerletPhysics physics ^VerletParticle particle1 ^VerletParticle particle2 len strength]
  (.addSpring physics
              (VerletSpring. particle1 particle2 len strength)))

(defn ^VerletPhysics add-min-distance-spring
  "Adds a spring to the physics world, connecting two particles in space. The string which
  will only enforce its rest length if the current distance is less than its rest length.
  The simulation takes particle weight into account."
  [^VerletPhysics physics ^VerletParticle particle1 ^VerletParticle particle2 rest-len strength]
  (.addSpring physics
              (VerletMinDistanceSpring. particle1 particle2 rest-len strength)))

(defn ^VerletPhysics update
  "Progresses the physics simulation by one time step and updates all forces
  and particle positions accordingly."
  [^VerletPhysics physics]
  (.update physics))
