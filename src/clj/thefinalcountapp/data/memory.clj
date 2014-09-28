(ns thefinalcountapp.data.memory
  (:require [com.stuartsierra.component :as component]
            [thefinalcountapp.data.store :as s]
            [clj-time.core :as time]))


(defrecord InMemoryRefDatabase [store]
  component/Lifecycle
  (start [this]
    this)

  (stop [this]
    (dosync
      (ref-set store {}))
    this)

  s/Store
  (create-group [_ name]
    (let [gr {:name name
              :counters []}]
      (dosync
        (alter store assoc name gr))))


  (get-group [_ group]
    (@store group))


  (group-exists? [_ group]
    (contains? @store group))


  (create-counter [_ group counter]
    (let [c (assoc counter :id (java.util.UUID/randomUUID))]
      (dosync
       (alter store (fn [groups]
                      (update-in groups [group :counters] #(conj % c)))))
      c))


  (counter-exists? [_ group id]
    (when-let [gr (s/get-group _ group)]
      (some #(.equals id (:id %)) (:counters gr))))


  (get-counter [_ group id]
    (when-let [gr (s/get-group _ group)]
      (first (filter #(= id (:id %)) (:counters gr)))))


  (update-counter [_ group counter-id new-counter]
    (dosync
      (let [old-counter (s/get-counter _ group counter-id)
            counters (vec (filter #(not= counter-id (:id %)) (:counters (s/get-group _ group))))
            updated-counter (merge old-counter new-counter)
            new-counters (conj counters updated-counter)]
       (alter store assoc-in [group :counters] new-counters)
       updated-counter)))

  (increment-counter [_ group counter-id]
    (let [old-counter (s/get-counter _ group counter-id)]
     (s/update-counter _ group counter-id {:value (inc (:value old-counter))})))

  (reset-counter [_ group counter-id]
    (let [old-counter (s/get-counter _ group counter-id)]
      (if (= :counter (:type old-counter))
        (s/update-counter _ group counter-id {:value 0 :last-updated (time/now)})
        (s/update-counter _ group counter-id {:last-updated (time/now)}))))

  (delete-counter [_ group counter-id]
    (dosync
      (let [new-counters (vec (filter #(not= counter-id (:id %)) (:counters (s/get-group _ group))))]
        (alter store assoc-in [group :counters] new-counters)))))


(defn in-memory-store [initial-data]
  (->InMemoryRefDatabase (ref initial-data)))
