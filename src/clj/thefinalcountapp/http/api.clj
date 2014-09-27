(ns thefinalcountapp.http.api
  (:require [compojure.core :refer [defroutes ANY]]
            [com.stuartsierra.component :as component]
            [ring.middleware.defaults :as ring-defaults]
            [thefinalcountapp.data :as data]
            [liberator.core :refer [defresource]]
            [io.clojure.liberator-transit]))

;; Resources
(def resource-defaults
  {:available-media-types ["application/transit+json" "application/json"]})

(defresource group-list []
  resource-defaults
  :allowed-methods [:post]
  :post! (fn [ctx]
           (let [body (get-in ctx [:request :body])
                 group (:group body)
                 db (::db ctx)]
             {::entity (data/create-group db group)}))
  :post-redirect? false
  :new? false
  :respond-with-entity? true
  :multiple-representations? false
  :handle-ok ::entity)

(defresource group-detail [group]
  resource-defaults
  :allowed-methods [:get]
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (data/get-group db group))))


(defresource counter-detail [group counter-id]
  resource-defaults
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (data/get-counter db group counter-id))))


;; Routes
(defroutes api-routes
  (ANY "/api/counters" [] (group-list))
  (ANY "/api/counters/:group" [group] (group-detail group))
  (ANY "/api/counters/:group/:id" [group id] (counter-detail group id)))


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
