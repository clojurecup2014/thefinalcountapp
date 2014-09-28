(ns thefinalcountapp.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [taoensso.sente  :as sente]
            [thefinalcountapp.http :as http]
            [thefinalcountapp.state :as state]
            [cljs.core.async :as async :refer [<!]]))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {:type :auto})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
)

;; Events
(defmulti event-handler (comp first :?data))

;; (defmethod event-handler :group/subscribed
;;   [{:as ev :keys [?data]}]
;;   (let [[ev group] ?data]
;;     (.log js/console "Subscribed to " group)))
;;
;; (defmethod event-handler :group/unsubscribed
;;   [{:as ev :keys [?data]}]
;;   (let [[ev group] ?data]
;;     (.log js/console "Unsubscribed from " group)))

(defmethod event-handler :counter/created
  [{:as ev :keys [?data]}]
  (let [[ev {:keys [group id]}] ?data]
    (go
      (let [counter (:data (<! (http/get (str "/api/counters/kaleidos-team/" id))))]
        (state/add-counter counter)))))

(defmethod event-handler :counter/updated
  [{:as ev :keys [?data]}]
  (let [[ev {:keys [group id]}] ?data]
    (go
      (let [counter (:data (<! (http/get (str "/api/counters/kaleidos-team/" id))))]
        (state/update-counter counter)))))

(defmethod event-handler :counter/deleted
  [{:as ev :keys [?data]}]
  (let [[ev {:keys [group id]}] ?data]
    (state/delete-counter id)))

(defmethod event-handler :default
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  nil)

;;; Client actions
(defn subscribe [group]
  (chsk-send! [:group/subscribe {:group group :uid (:uid @chsk-state)}]))

(defn unsubscribe [group]
  (chsk-send! [:group/unsubscribe {:group group :uid (:uid @chsk-state)}]))

;; Subscribe
(defn start-event-system []
  (sente/start-chsk-router! ch-chsk event-handler)
  (go (<! (async/timeout 1000))
      (subscribe "kaleidos-team")))
