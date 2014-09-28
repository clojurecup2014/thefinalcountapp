(ns thefinalcountapp.state
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [thefinalcountapp.http :as http]
            [thefinalcountapp.time :as time]
            [cljs.core.async :as async :refer [<!]]))

(def state (atom {:group "kaleidos-team"
                  :counters []
                  :displaying 0}))

(defn index-of [coll v]
  (let [i (count (take-while #(not= v (:id %)) coll))]
    (when (or (< i (count coll))
              (= v (:id (last coll))))
      i)))

(defn add-counter [counter]
  (swap! state #(update-in % [:counters] conj counter)))

(defn update-counter [counter]
  (let [counter-idx (index-of (:counters @state) (:id counter))]
    (swap! state #(update-in % [:counters] assoc counter-idx counter))))

(defn delete-counter [counter-id]
    (let [new-counters (vec (filter #(not= counter-id (:id %)) (:counters @state)))]
      (swap! state #(assoc-in % [:counters] new-counters))))
