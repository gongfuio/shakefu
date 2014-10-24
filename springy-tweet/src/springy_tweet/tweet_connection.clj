(ns springy-tweet.tweet-connection
  (:require [org.httpkit.client :as http]
			[cheshire.core :as cheshire]
			[overtone.at-at :as at-at]))

; url pointing to a node-js server
; to be replaced by a clojure based implementation
(def ^:private tweet-server-url "http://nomethod.ch:3000/tweetFull")

(defn ^:private get-tweet
  ; async http call
  [sucess-handler error-handler]
  (http/get tweet-server-url {}
          (fn [{:keys [status headers body error]}] ;; asynchronous response handling
            (if error
              (error-handler error)
              (sucess-handler (cheshire/parse-string body keyword))))))

(defn connect-tweet-server
	"returns an overtone.at-at scheduler calling the tweet server at set intervals
	rate -> interval in ms between each calls"
  ([at-at-pool rate success-handler]
   (connect-tweet-server at-at-pool rate success-handler (fn[e]())))
  ([at-at-pool rate success-handler error-handler]
  (at-at/every rate #(get-tweet success-handler error-handler) at-at-pool :fixed-delay true)))

(defn stop-connection
  [connection]
  (at-at/stop connection))
