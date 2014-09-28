(ns thefinalcountapp.http.api
  (:require [compojure.core :refer [defroutes GET POST PUT ANY DELETE]]
            [com.stuartsierra.component :as component]
            [cognitect.transit :as transit]
            [ring.middleware.defaults :as ring-defaults]
            [thefinalcountapp.time :as time]
            [thefinalcountapp.data.store :as store]
            [thefinalcountapp.data.schemas :as schemas]
            [thefinalcountapp.http.pubsub :as pubsub]
            [schema.core :refer [check]]
            [liberator.core :refer [defresource]]
            [cognitect.transit :as transit]
            [io.clojure.liberator-transit :as lt])
  (:import [org.joda.time DateTime]))

;; Resources
(def resource-defaults
  {:available-media-types ["application/transit+json"]
   :as-response (lt/as-response {:handlers {DateTime time/joda-time-writer}})})


(defresource group-creation []
  resource-defaults
  :allowed-methods [:post]
  :malformed? (fn [ctx]
                (let [body (get-in ctx [:request :body])]
                  (when (nil? (check schemas/NewGroup body))
                    {::parsed-entity body})))
  :authorized? (fn [ctx]
                 (let [group (::parsed-entity ctx)
                       req (:request ctx)
                       db (::db req)]
                   (not (store/group-exists? db (:name group)))))
  :post! (fn [ctx]
           (let [group (::parsed-entity ctx)
                 req (:request ctx)
                 db (::db req)]
             {::entity (store/create-group db (:name group))}))
  :post-redirect? false
  :new? false
  :respond-with-entity? true
  :multiple-representations? false
  :handle-ok ::entity)


(defresource group-detail [group]
  resource-defaults
  :allowed-methods [:get]
  :exists? (fn [ctx]
             (let [req (:request ctx)
                   db (::db req)]
                 (store/group-exists? db group)))
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (store/get-group db group))))


(defresource counter-detail [group counter-id]
  resource-defaults
  :allowed-methods [:get]
  :exists? (fn [ctx]
             (let [req (:request ctx)
                   db (::db req)]
                 (store/counter-exists? db group counter-id)))
  :handle-ok (fn [ctx]
               (let [req (:request ctx)
                     db (::db req)]
                 (store/get-counter db group counter-id))))


(defresource counter-create [group]
  resource-defaults
  :allowed-methods [:post]
  :authorized? (fn [ctx]
                 (let [req (:request ctx)
                       db (::db req)]
                   (store/group-exists? db group)))
  ; FIXME: for some reason the body doesn't get converted from transit
  :post! (fn [ctx]
           (let [counter (get-in ctx [:request :body])
                 counter (transit/read (transit/reader counter :json))
                 req (:request ctx)
                 db (::db req)
                 created-counter (store/create-counter db group counter)]
             (pubsub/notify :counter/created group {:group group :id (:id created-counter)})
             {::entity created-counter}))
  :post-redirect? false
  :new? false
  :respond-with-entity? true
  :multiple-representations? false)


(defresource counter-update [group counter-id]
  resource-defaults
  :allowed-methods [:put]
  :exists? (fn [ctx]
             (let [req (:request ctx)
                   db (::db req)]
                 (store/counter-exists? db group counter-id)))
  ; FIXME: for some reason the body doesn't get converted from transit
  :put! (fn [ctx]
          (let [body (get-in ctx [:request :body])
                counter (transit/read (transit/reader body :json))
                req (:request ctx)
                db (::db req)
                updated-counter (store/update-counter db group counter-id counter)]
            (pubsub/notify :counter/updated group {:group group :id (:id counter)})
            {::entity updated-counter}))
  :conflict? false
  :post-redirect? false
  :new? false
  :respond-with-entity? true
  :multiple-representations? false)


(defresource counter-delete [group counter-id]
  resource-defaults
  :allowed-methods [:delete]
  :exists? (fn [ctx]
             (let [req (:request ctx)
                   db (::db req)]
                 (store/counter-exists? db group counter-id)))
  :delete! (fn [ctx]
             (let [req (:request ctx)
                   db (::db req)]
              (store/delete-counter db group counter-id)
              (pubsub/notify :counter/deleted group {:group group :id counter-id}))))

(defresource counter-increment [group counter-id]
  resource-defaults
  :allowed-methods [:post]
  :exists? (fn [ctx]
             (let [db (::db (:request ctx))]
               (store/counter-exists? db group counter-id)))
  :authorized? (fn [ctx]
                 (let [db (::db (:request ctx))]
                   (= :counter (:type (store/get-counter db group counter-id)))))
  :post! (fn [ctx]
           (let [db (::db (:request ctx))]
             (store/increment-counter db group counter-id)
             (pubsub/notify :counter/updated group {:group group :id counter-id})))
  :post-redirect? false
  :new? false
  :respond-with-entity? false)


(defresource counter-reset [group counter-id]
  resource-defaults
  :allowed-methods [:post]
  :exists? (fn [ctx]
             (let [db (::db (:request ctx))]
               (store/counter-exists? db group counter-id)))
  :post! (fn [ctx]
           (let [db (::db (:request ctx))]
             (store/reset-counter db group counter-id)
             (pubsub/notify :counter/updated group {:group group :id counter-id})))
  :post-redirect? false
  :new? false
  :respond-with-entity? false)


;; Routes
(defroutes api-routes
  (POST "/api/counters" [] (group-creation))
  (GET "/api/counters/:group" [group] (group-detail group))
  (POST "/api/counters/:group" [group] (counter-create group))
  (GET "/api/counters/:group/:id" [group id] (counter-detail group (java.util.UUID/fromString id)))
  (PUT "/api/counters/:group/:id" [group id] (counter-update group (java.util.UUID/fromString id)))
  (POST "/api/counters/:group/:id/increment" [group id] (counter-increment group (java.util.UUID/fromString id)))
  (POST "/api/counters/:group/:id/reset" [group id] (counter-reset group (java.util.UUID/fromString id)))
  (DELETE "/api/counters/:group/:id" [group id] (counter-delete group (java.util.UUID/fromString id))))


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
