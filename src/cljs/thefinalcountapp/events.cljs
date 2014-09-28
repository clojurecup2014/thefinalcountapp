(ns thefinalcountapp.events
  (:require [taoensso.sente  :as sente]))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {:type :auto})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
)

;; Events
(defmulti event-handler (comp first :?data))

(defmethod event-handler :group/subscribed
  [{:as ev :keys [?data]}]
  (let [[ev group] ?data]
    (.log js/console "Subscribed to " group)))

(defmethod event-handler :group/unsubscribed
  [{:as ev :keys [?data]}]
  (let [[ev group] ?data]
    (.log js/console "Unsubscribed from " group)))

(defmethod event-handler :counter/created
  [{:as ev :keys [?data]}]
  (let [[ev {:keys [group id]}] ?data]
    (.log js/console "Created counter with id " id " in group " group)))

(defmethod event-handler :counter/updated
  [{:as ev :keys [?data]}]
  (let [[ev {:keys [group id]}] ?data]
    (.log js/console "Updated counter with id " id " in group " group)))

(defmethod event-handler :counter/deleted
  [{:as ev :keys [?data]}]
  (let [[ev {:keys [group id]}] ?data]
    (.log js/console "Deleted counter with id " id " in group " group)))

(defmethod event-handler :default
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (.log js/console "ev ")
  (.log js/console id))

;;; Client actions
(defn subscribe [group]
  (chsk-send! [:group/subscribe {:group group :uid (:uid @chsk-state)}]))

(defn unsubscribe [group]
  (chsk-send! [:group/unsubscribe {:group group :uid (:uid @chsk-state)}]))


;; Subscribe
(add-watch chsk-state :connection (fn [key reference old-state new-state]
                                    (subscribe "kaleidos-team"
                                               8000
                                               (fn [reply]
                                                 (when (sente/cb-success reply)
                                                   (remove-watch chsk-state :connection))))))


(sente/start-chsk-router! ch-chsk event-handler)
