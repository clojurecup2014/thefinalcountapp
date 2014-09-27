(ns thefinalcountapp.data.query
  (:import  [java.util Date UUID]))

;; Counters types:
;; :count-up -> Days without...
;; :streak -> Github daily count...
;; :counter -> Times you've broken the repo
(defonce db (atom { "kaleidos-team" {:counters [{:id 1 :type :count-up :value 321 :text "somebody messing up with the git repo" :last-updated (Date.) :public-reset true}
                                                {:id 2 :type :streak   :value 350 :text "daily Github commit" :last-updated (Date.) :public-reset true}
                                                {:id 3 :type :counter  :value 123 :text "we've broken the GIT repo' " :last-updated (Date.) :public-reset false :public-plus true}]
                                     :name "Kaleidos Team"}}))

(defn create-group [_ group]
  (swap! db #(assoc % group []))
  {:name group
   :counters (@db group)})


(defn get-group [_ group]
  (@db group))


(defn create-counter [_ group counter]
  (let [c (assoc counter :id (Math/abs (.nextInt (java.util.Random.))))]
    (swap! db (fn [groups]
                (update-in groups [group :counters] #(conj % c))))
    counter))


(defn group-exists? [_ group]
  (contains? @db group))


(defn counter-exists? [_ group id]
  (when-let [gr (get-group _ group)]
    (some #(= id (:id %)) (:counters gr))))


(defn get-counter [_ group id]
  (when-let [gr (get-group _ group)]
    (first (filter #(= id (:id %)) (:counters gr)))))


(defn update-counter [_ group counter-id new-counter]
  (let [old-counter (get-counter _ group counter-id)
        counters (vec (filter #(not= counter-id (:id %)) (:counters (get-group _ group))))
        updated-counter (merge old-counter new-counter)
        new-counters (conj counters updated-counter)]
   (swap! db #(assoc-in % [group :counters] new-counters))
   updated-counter))
