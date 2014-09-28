(ns thefinalcountapp.http.pubsub
  (:import [java.util UUID])
  (:require [taoensso.sente :as sente]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.core.async :as async :refer [go <! >! go-loop]]
            [ring.middleware.defaults]
            [com.stuartsierra.component :as component]))


(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {:user-id-fn (fn [_] (UUID/randomUUID))})]
  ;; Ring handlers
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  ;; Channels
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  ;; Connected clients
  (def connected-uids                connected-uids) ; Watchable, read-only atom
)

; TODO: connected-uids on disconnect, unsubscribe from all

;; Subscriptions
(def subscriptions (atom {}))

(defn subscribe [uid group]
  (swap! subscriptions (fn [subs]
                         (update-in subs [uid] (fnil #(conj % group) #{})))))

(defn subscribed? [uid group]
  (if-let [subs (@subscriptions uid)]
    (contains? subs group)
    false))

(defn unsubscribe [uid group]
  (when (contains? @subscriptions uid)
    (if (= 1 (count  (@subscriptions uid)))
      (swap! subscriptions (fn [subs]
                             (dissoc subs uid)))
      (swap! subscriptions (fn [subs]
                             (update-in subs [uid] #(disj % group)))))))


;; Routes
(defroutes pubsub-routes
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))


;; Notifications
(defn broadcast [value]
  (doseq [uid (:any @connected-uids)]
    (chsk-send! uid
                [:some/broadcast
                 {:value value}])))

;; Events
(defmulti event-handler :id)

(defmethod event-handler :group/subscribe
  [{:keys [?data]}]
  (let [{:keys [group uid]} ?data]
    (subscribe uid group)
    (chsk-send! uid [:group/subscribed group])))

(defmethod event-handler :group/unsubscribe
  [{:keys [?data]}]
  (let [{:keys [group uid]} ?data]
    (unsubscribe uid group)
    (chsk-send! uid [:group/unsubscribed group])))

(defmethod event-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  nil)


;; Component
(defrecord PubSub [shutdown]
  component/Lifecycle
  (start [this]
    ;; Routes
    (assoc this :routes (ring.middleware.defaults/wrap-defaults pubsub-routes ring.middleware.defaults/site-defaults)
                :shutdown (sente/start-chsk-router! ch-chsk event-handler)))

  (stop [this]
    (shutdown)
    (dissoc this :routes :shutdown)))


(defn pubsub []
  (map->PubSub {}))
