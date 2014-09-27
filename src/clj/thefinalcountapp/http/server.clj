(ns thefinalcountapp.http.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]))


(defrecord  HTTPServer [opts routes]
  component/Lifecycle
  (start [this]
    (println "Starting HTTP server...")
    (assoc this :server (httpkit/run-server (:routes routes) opts)))

  (stop [this]
    (when-let [server (:server this)]
      (println "Stopping HTTP server...")
      (server)
      (dissoc this :server))))
