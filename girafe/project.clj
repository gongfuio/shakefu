(defproject girafe "0.1.0-SNAPSHOT"
  :description "A visualization of tweets and their authors as a force-directed graph"
  :url "https://github.com/gongfuio/shakefu/girafe"
  :license {:name "Creative Commons Attribution-ShareAlike 4.0 International"
            :url "http://creativecommons.org/licenses/by-sa/4.0/"}
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [quil "2.2.2"]
    [toxiclibs/toxiclibscore "0020"]
    [toxiclibs/toxiclibs-p5  "0003"]
    [toxiclibs/verletphysics "0010"]]
  :main girafe.core)
