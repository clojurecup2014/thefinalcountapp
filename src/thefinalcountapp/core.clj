(ns thefinalcountapp.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [org.httpkit.server :as kit]
            [ring.middleware.defaults :as ring-defaults]
            [ring.util.response :as response]))

;; Handlers

(defn redirect-index
  [req]
  (response/redirect "/index.html"))

;; Routes

(defroutes app-routes
  (GET "/" [] redirect-index)
  (route/resources "/"))

;; Server

(def app (ring-defaults/wrap-defaults #'app-routes ring-defaults/site-defaults))

(defn start-server []
  (kit/run-server (var app) {:port 8080}))

(defn -main [& args]
  (println "Starting server...")
  (start-server))
