(ns thefinalcountapp.http.routes
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.core :refer [routes]]
            [com.stuartsierra.component :as component]
            [ring.util.response :as response]))

;; Routes
(defroutes base-routes
  (GET "/" [] (response/redirect "index.html"))
  (route/resources "/"))


;; Component
(defrecord Routes [api pubsub]
  component/Lifecycle
  (start [this]
    (assoc this :routes (routes base-routes
                                (:routes api)
                                (:routes pubsub))))

  (stop [this]
    (dissoc this :routes)))
