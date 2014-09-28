(ns thefinalcountapp.time
  (:require [cljs-time.core :as time]))


(defn days-since [datetime]
  (time/in-days (time/interval datetime (time/now))))
