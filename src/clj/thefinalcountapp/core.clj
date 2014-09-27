(ns thefinalcountapp.core
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :as route]
            [org.httpkit.server :as kit]
            [ring.middleware.defaults :as ring-defaults]
            [ring.util.response :as response]
            [liberator.core :refer [defresource]]
            [io.clojure.liberator-transit]))

;; Resources
(defresource counters-resource
  :available-media-types ["application/transit+json"
                          "application/transit+msgpack"
                          "application/json"]
  :handle-ok [{:text "Days without messing up" :value 100}])

;; Handlers
(defn redirect-index
  [req]
  (response/redirect "/index.html"))

;; Routes

(defroutes app-routes
  (ANY "/api/counters" [] counters-resource)
  (GET "/" [] redirect-index)
  (route/resources "/"))

;; Server

(def app (ring-defaults/wrap-defaults #'app-routes ring-defaults/site-defaults))

(defn start-server []
  (kit/run-server (var app) {:port 8080}))

(defn -main [& args]
  (println "Starting server...")
  (start-server))
