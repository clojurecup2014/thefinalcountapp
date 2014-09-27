(ns thefinalcountapp.http.api
  (:require [compojure.core :refer [defroutes GET POST PUT ANY]]
            [com.stuartsierra.component :as component]
            [cognitect.transit :as transit]
            [ring.middleware.defaults :as ring-defaults]
            [thefinalcountapp.data.query :as query]
            [liberator.core :refer [defresource]]
            [io.clojure.liberator-transit]))

;; Resources
(def resource-defaults
  {:available-media-types ["application/transit+json"]})


(defresource group-creation []
  resource-defaults
  :allowed-methods [:post]
  :authorized? (fn [ctx]
                 (let [body (get-in ctx [:request :body])
                       group (:group body)
                       db (::db ctx)]
                   (not (query/group-exists? db group))))
  :post! (fn [ctx]
           (let [body (get-in ctx [:request :body])
                 group (:group body)
                 db (::db ctx)]
             {::entity (query/create-group db group)}))
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
                 (query/get-group db group))))


(defresource counter-detail [group counter-id]
  resource-defaults
  :allowed-methods [:get]
  :exists? (fn [ctx]
             (let [req (:request ctx)
                   db (::db req)]
                 (query/counter-exists? db group  (Integer/parseInt counter-id))))
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (query/get-counter db group (Integer/parseInt counter-id)))))


(defresource counter-create [group]
  resource-defaults
  :allowed-methods [:post]
  :post! (fn [ctx]
           (let [body (get-in ctx [:request :body])
                 counter (transit/read (transit/reader body :json))
                 db (::db ctx)]
             {::entity (query/create-counter db group counter)}))
  :post-redirect? false
  :new? false
  :respond-with-entity? true
  :multiple-representations? false
  :handle-ok ::entity)


;; Routes
(defroutes api-routes
  (POST "/api/counters" [] (group-creation))
  (GET "/api/counters/:group" [group] (group-detail group))
  (POST "/api/counters/:group" [group] (counter-create group))
  (GET ["/api/counters/:group/:id", :id #"[0-9]+"] [group id] (counter-detail group id)))


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
