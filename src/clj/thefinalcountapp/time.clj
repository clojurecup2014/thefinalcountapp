(ns thefinalcountapp.time
  (:require [clj-time.core :as time]
            [clj-time.coerce :as coerce]
            [cognitect.transit :as transit]))

; Thanks to http://increasinglyfunctional.com/2014/09/02/custom-transit-writers-clojure-joda-time/
(def joda-time-writer
  (transit/write-handler
   (constantly "m")
   (fn [v] (-> v coerce/to-date .getTime))
   (fn [v] (-> v coerce/to-date .getTime .toString))))
