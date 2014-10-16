(ns insta.core
  (:require
    [clojure.inspector :refer :all]
    [instagram.oauth :as oauth]
    [instagram.callbacks :as callbacks]
    [instagram.callbacks.handlers :as handlers]
    [instagram.api.endpoint :as endpoint])
  (:import
    (instagram.callbacks.protocols SyncSingleCallback)))

(def ^:dynamic *creds* (oauth/make-oauth-creds "c140704982414cfb9468dbbbbdc4d6e4"
                                         "7187b8f7ca774ade8e74286a1d61e115"
                                        "http://www.darksite.ch/kundig/insta.php"))

;(get-popular :oauth *creds*)

(defn tag-images
  "Cette fonction fait quelquechose avec un flux Instagram, mais quoi exactement?"
  [tag]
  (let [tagged-medias (endpoint/get-tagged-medias :oauth *creds* :params {:tag_name tag})]
    { :datas (get-in tagged-medias [:body "data"])}))

; {
;   :id ""
;   :caption ""
;   :user ""
;   :location { :latitude "..."
;               :longitude "..." }
;   :images [ ...]
;   :etc }
     ;;(map #(get-in % ["images" "low_resolution" "url"])   datas)
    ;(get-in datas [0 "images" "low_resolution" "url"] )
    ;;))

;; $ lein repl
;; (require '[insta.core :as i] :reload)
;; (require '[clojure.inspector :refer :all])
;; (inspect-tree (i/tag-images "truc"))