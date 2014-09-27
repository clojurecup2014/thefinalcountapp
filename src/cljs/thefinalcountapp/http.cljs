(ns thefinalcountapp.http
  (:require [goog.events :as events]
            [goog.dom :as gdom]
            [cljs.core.async :refer [chan put!]]
            [cognitect.transit :as t])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType])
  (:refer-clojure :exclude [get]))


(def transit-content-type "application/transit+json")


(def headers #js {"Content-Type" transit-content-type
                  "Accept"       transit-content-type})


(def ^:private method-key->method-string
  {:get "GET"
   :put "PUT"
   :post "POST"
   :delete "DELETE"})


(defn send [{:keys [method url data]}]
  (let [c (chan)
        xhr (XhrIo.)
        method (method-key->method-string method)
        data (when data (t/write (t/writer :json) data))]

    (try
      (.send xhr url
                 method
                 data
                 headers)
      (catch js/Error e
        (put! c {:result :error
                 :message (str (.-message e))})))

    (events/listen xhr goog.net.EventType.SUCCESS
                   (fn [e] (put! c (let [response (.getResponseText xhr)
                                         response-obj (if (not-empty response) (t/read (t/reader :json) response) nil)]
                                     (assoc {} :result :success :data response-obj)))))
    (events/listen xhr goog.net.EventType.ERROR
                   (fn [e] (put! c {:result :error
                                    :message (str (.formatMsg_ xhr "Sending request")
                                               (.getResponseText xhr))})))
    c))



(defn get [url]
  (send {:method :get, :url url}))


(defn post [url data]
  (send {:method :post, :url url, :data data}))


(defn put [url data]
  (send {:method :put, :url url, :data data}))


(defn delete [url]
  (send {:method :delete, :url url}))
