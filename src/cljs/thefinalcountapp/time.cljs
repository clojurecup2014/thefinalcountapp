(ns thefinalcountapp.time
  (:require [cljs-time.core :as time]
            [cljs-time.coerce :as coerce]))


(defn days-since [raw-time]
  (if-not (nil? raw-time)
    (let [now (time/now)
          datetime (coerce/from-date raw-time)]
      (if (time/after? datetime now)
        (time/in-days (time/interval now datetime))
        (time/in-days (time/interval datetime now))))
    0))
