(defproject springy-tweet "0.1.0-SNAPSHOT"
  :description "A sample Quil sketch for Andreas, designed to start using Clojure, Leiningen and Processing :-)"
  :url "https://github.com/gongfuio/shakefu/hello-quil"
  :license {
    :name "Eclipse Public License"
    :url "http://www.eclipse.org/legal/epl-v10.html" }
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [quil "2.2.2"]
	[http-kit "2.1.16"]
    [cheshire "5.3.1"]
	[overtone/at-at "1.2.0"]
	[tween-clj "0.5.0"]]
  :main hello-tweet.core)
