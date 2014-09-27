(ns thefinalcountapp.http.api
  (:require [compojure.core :refer [defroutes ANY]]
            [com.stuartsierra.component :as component]
            [ring.middleware.defaults :as ring-defaults]
            [thefinalcountapp.db :as db]
            [liberator.core :refer [defresource]]
            [io.clojure.liberator-transit]))

;; Resources
(defresource counters-resource [group]
  :available-media-types ["application/transit+json" "application/json"]
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (db/get-group db group))))


(defresource counters-resource-detail [group counter-id]
  :available-media-types ["application/transit+json" "application/json"]
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (db/get-counter db group counter-id))))


;; Routes
(defroutes api-routes
  (ANY "/api/counters/:group" [group] (counters-resource group))
  (ANY "/api/counters/:group/:id" [group id] (counters-resource-detail group id)))


;; Component
(defn api-middleware [handler db]
  (fn [req]
    (handler (assoc req ::db db))))


(defrecord API [db]
  component/Lifecycle
  (start [this]
    (let [wrapped-api-routes (ring-defaults/wrap-defaults #'api-routes ring-defaults/api-defaults)]
      (assoc this :routes (api-middleware wrapped-api-routes db))))

  (stop [this]
    (dissoc this :routes)))
