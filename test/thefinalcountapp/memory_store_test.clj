(ns thefinalcountapp.memory-store-test
  (:require [clojure.test :refer :all]
            [thefinalcountapp.data.store :as store]
            [thefinalcountapp.data.memory :as memstore]
            [com.stuartsierra.component :as component]))

(defn make-store []
  (memstore/->InMemoryRefDatabase (ref {})))


(deftest groups
  (testing "A group can be created and retrieved"
    (let [store (make-store)
          name "Kaleidos Team"
          group {:name name, :counters []}]
       (is (not (store/group-exists? store name)))
       (store/create-group store name)
       (is (= group (store/get-group store name)))
       (is (store/group-exists? store name)))))

(deftest counters
  (testing "A counter can be added to a group, retrieved, updated and deleted")
    (let [store (make-store)
          gr "Kaleidos Team"
          _ (store/create-group store name)
          c (store/create-counter store gr {:type :streak
                                            :text "Daily commit"
                                            :value 42})
          cid (:id c)]

       (is (store/counter-exists? store gr cid))
       (is (= c (store/get-counter store gr cid)))
       (store/update-counter store gr cid {:value 0})
       (is (zero? (:value (store/get-counter store gr cid))))
       (store/delete-counter store gr cid)
       (is (not (store/counter-exists? store gr cid)))))
