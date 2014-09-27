(ns thefinalcountapp.core
  (:import  [java.util Date])
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :as route]
            [org.httpkit.server :as kit]
            [ring.middleware.defaults :as ring-defaults]
            [ring.util.response :as response]
            [liberator.core :refer [defresource]]
            [io.clojure.liberator-transit]))

;; Counters types:
;; :count-up -> Days without...
;; :streak -> Github daily count...
;; :counter -> Times you've broken the repo
(def db (atom { "kaleidos-team"
                [{:id 1 :type :count-up :value 100 :text "somebody messing up" :last-updated (Date.) :public-reset true}
                 {:id 2 :type :streak   :value 350 :text "daily Github commit" :last-updated (Date.) :public-reset true}
                 {:id 3 :type :counter  :value 100 :text "we've broken the GIT repo' " :last-updated (Date.) :public-reset false :public-plus true}]}))

;; Resources
(defresource counters-resource [group]
  :available-media-types ["application/transit+json" "application/json"]
  :handle-ok (@db group))

(defresource counters-resource-detail [group id]
  :available-media-types ["application/transit+json" "application/json"]
  :handle-ok (first (@db group)))

;; Handlers
(defn redirect-index
  [req]
  (response/redirect "/index.html"))

;; Routes

(defroutes app-routes
  (ANY "/api/counters/:group" [group] (counters-resource group))
  (ANY "/api/counters/:group/:id" [group id] (counters-resource-detail group id))
  (GET "/" [] redirect-index)
  (route/resources "/"))

;; Server

(def app (ring-defaults/wrap-defaults #'app-routes ring-defaults/site-defaults))

(defn start-server []
  (kit/run-server (var app) {:port 8080}))

(defn -main [& args]
  (println "Starting server...")
  (start-server))
