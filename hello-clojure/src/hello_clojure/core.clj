(ns hello-clojure.core
  (:require [clojure.inspector :refer :all]))

(def nice-things
    { :conferences [ "Soft-Shake" ]
      :beers [
        "La comète"
        "Pilsner" ]
      :books [
        { :title "Un coup de vent contre un bout de viande" :author "Andreas Künding" }
        { :title "L'Île ou Eloge des voyages insensés" :author "Vassili Golovanov" }
        { :title "Flatland: A Romance of Many Dimensions" :author "Edwin A. Abbott" } ]})

(defn show-me [what]
  (inspect-tree what))

;; Pour invoquer ce code depuis le REPL:
;;
;; $ lein repl
;; nREPL server started on port 50013 on host 127.0.0.1
;; REPL-y 0.3.0
;; Clojure 1.6.0
;; ...
;; hello-clojure.core=> (require '[hello-clojure :as hello] :reload :verbose)
;; nil
;; hello-clojure.core=> (hello/show-me nice-things)
;; ...
;; hello-clojure.core=> (pprint nice-things) 
;; ...
