(ns thefinalcountapp.data
  (:import  [java.util Date])
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :as route]
            [thefinalcountapp.http.server :as server]
            [com.stuartsierra.component :as component]
            [ring.middleware.defaults :as ring-defaults]
            [ring.util.response :as response]
            [liberator.core :refer [defresource]]
            [io.clojure.liberator-transit]))

;; Counters types:
;; :count-up -> Days without...
;; :streak -> Github daily count...
;; :counter -> Times you've broken the repo
(def db (atom { "kaleidos-team" {:counters [{:id 1 :type :count-up :value 100 :text "somebody messing up" :last-updated (Date.) :public-reset true}
                                            {:id 2 :type :streak   :value 350 :text "daily Github commit" :last-updated (Date.) :public-reset true}
                                            {:id 3 :type :counter  :value 100 :text "we've broken the GIT repo' " :last-updated (Date.) :public-reset false :public-plus true}]
                                 :name "Kaleidos Team"}}))


(defrecord Database [dbspec]
  component/Lifecycle
  (start [this]
    ; TOOD connection pooling when we have a proper DB
    (assoc this :pool dbspec))

  (stop [this]
    (dissoc this :pool)))

; Public API

(defn create-group [_ group-name]
  (swap! db #(assoc % group-name []))
  {:name group-name
   :counters (@db group-name)})


(defn get-group [_ group-name]
  (get-in @db [group-name :counters]))


(defn create-counter [_ group-name counter]
  (let [counter (assoc counter :id (str (java.util.UUID/randomUUID)))]
    (swap! db (fn [groups]
                (update-in groups [group-name :counters] #(conj % counter))))
    counter))


(defn group-exists? [_ group]
  (contains? @db group))


(defn get-counter [_ group-name counter-id]
  (first (@db group-name)))


(defn update-counter [_ group-name counter-id new-counter]
  ; TODO
  {:id 1
   :type :count-up
   :value 100
   :text "somebody messing up"
   :last-updated (Date.)
   :public-reset true})
