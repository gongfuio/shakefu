(ns hello-quil.p)

(def a (atom clojure.lang.PersistentQueue/EMPTY))

(swap! a conj 1)

(pop @a)

(subvec [1 2 3] 1)
