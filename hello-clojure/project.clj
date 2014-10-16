(defproject hello-clojure "0.1.0-SNAPSHOT"
  :description "Elementary Clojure app to show how to invoke it from the REPL"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot hello-clojure.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
