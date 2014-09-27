(ns thefinalcountapp.http
  (:require [goog.events :as events]
            [goog.dom :as gdom]
            [cljs.core.async :refer [chan put!]]
            [cognitect.transit :as t])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(def ^:private meths
  {:get "GET"
   :put "PUT"
   :post "POST"
   :delete "DELETE"})

(defn send [{:keys [method url data]}]
  (let [c (chan)
        xhr (XhrIo.)]
    (events/listen xhr goog.net.EventType.SUCCESS
                   (fn [e] (put! c (let [response (.getResponseText xhr)
                                         response-obj (if (not-empty response) (t/read (t/reader :json) response) nil)]
                                     (assoc {} :result :success :data response-obj)))))
    (events/listen xhr goog.net.EventType.ERROR
                   (fn [e] (put! c {:result :error
                                    :message (str (.formatMsg_ xhr "Sending request")
                                               (.getResponseText xhr))})))
    (try (. xhr (send url (meths method)
                      (when data (t/write (t/writer :json) data))
                      #js {"Content-Type" "application/transit+json" "Accept" "application/transit+json"}))
         (catch js/Error e (put! c {:result :error
                                    :message (str (.-message e))} )))
    c))
