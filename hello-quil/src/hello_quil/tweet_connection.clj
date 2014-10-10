(ns hello-quil.tweet-connection
  (:require [org.httpkit.client :as http]
			[overtone.at-at :as at-at]))

;(clojure.core/use '[clojure.repl :only (doc)])

(def ^:private tweet-server-url "http://nomethod.ch:3000/tweet")

(defn ^:private get-tweet
  ; async http call
  [sucess-handler error-handler]
  (http/get tweet-server-url {}
          (fn [{:keys [status headers body error]}] ;; asynchronous response handling
            (if error
              (error-handler error)
              (sucess-handler body)))))


; setup at-at

(def ^:private my-pool (at-at/mk-pool)) ; should this be injected into the module?

(defn connect-tweet-server
"returns an overtone.at-at scheduler calling the tweet server
at given intervals
rate -> interval in ms between each calls"
  ([rate success-handler]
   (connect-tweet-server rate success-handler (fn[e]())))
  ([rate success-handler error-handler]
  (at-at/every rate #(get-tweet success-handler error-handler) my-pool)))

(defn stop-connection
  [connection]
  (at-at/stop connection))
