(ns thefinalcountapp.http.pubsub
  (:import [java.util UUID])
  (:require [taoensso.sente :as sente]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.core.async :as async :refer [go <! >! go-loop]]
            [ring.middleware.defaults]
            [com.stuartsierra.component :as component]))


; (defonce subscriptions (atom {}))
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


(defroutes pubsub-routes
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))


(defn broadcast [num]
  (doseq [uid (:any @connected-uids)]
    (chsk-send! uid
                [:some/broadcast
                 {:num num}])))


(defrecord PubSub []
  component/Lifecycle
  (start [this]
    ;; Process incoming events
;    (go (let [ev (<! ch-chsk)]
;          (chsk-send! nil {:foo :bar})))
    ;; Broadcast
    (go-loop [i 0]
      (<! (async/timeout 1000))
      (broadcast i)
      (recur (inc i)))
    ;; Routes
    (assoc this :routes (ring.middleware.defaults/wrap-defaults pubsub-routes ring.middleware.defaults/site-defaults)))

  (stop [this]
    (dissoc this :routes)))


(defn pubsub []
  (->PubSub))
