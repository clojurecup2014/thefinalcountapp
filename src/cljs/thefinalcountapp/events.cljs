(ns thefinalcountapp.events
  (:require-macros
   [cljs.core.async.macros :as async :refer (go go-loop)])
  (:require
   [cljs.core.async :as async :refer (<! >! put! chan)]
   [taoensso.sente  :as sente :refer (cb-success?)]
  ))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
       {:type :auto ; e/o #{:auto :ajax :ws}
       })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
)


(defn subscribe [group]
  (chsk-send! [:group/subscribe group]))


(subscribe "kaleidos-team")

; TODO multimethod for handling events

; Client events
;  :group/subscribe

; Server events
;  :counter/updated {:group "kaleidos-team" :id 1}

(go-loop [ev (<! ch-chsk)]
           (.log js/console "event received: ")
           (.log js/console (str ev))
           (recur (<! ch-chsk)))
