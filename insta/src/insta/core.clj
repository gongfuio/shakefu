(ns insta.core
  (:use
    instagram.oauth
    instagram.callbacks
    instagram.callbacks.handlers
    instagram.api.endpoint)
  (:import
    (instagram.callbacks.protocols SyncSingleCallback)))

(def ^:dynamic *creds* (make-oauth-creds "c140704982414cfb9468dbbbbdc4d6e4"
                                         "7187b8f7ca774ade8e74286a1d61e115"
                                        "http://www.darksite.ch/kundig/insta.php"))

;(get-popular :oauth *creds*)

(((((((get-tagged-medias :oauth *creds* :params {:tag_name "clojure"})
      :body) "data") 0) "images") "low_resolution") "url")
