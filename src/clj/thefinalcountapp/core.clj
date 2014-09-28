(ns thefinalcountapp.core
  (:import [java.util Date])
  (:require [thefinalcountapp.http.server :as server]
            [thefinalcountapp.http.api :as api]
            [thefinalcountapp.http.routes :as routes]
            [clj-time.core :as time]
            ;;[thefinalcountapp.data.store :as store]
            [thefinalcountapp.data.memory :as memstore]
            [com.stuartsierra.component :as component]))

(defonce initial-data (atom {"kaleidos-team" {:counters [{:id 1 :color :red :type :count-up :text "somebody messing up with the git repo" :last-updated (time/date-time 2014 9 27) :public-reset true}
                                                         {:id 2 :color :yellow :type :streak   :text "daily Github commit" :last-updated (time/date-time 2014 8 3) :public-reset true}
                                                         {:id 3 :color :blue :type :counter  :value 42 :text "we've broken the GIT repo' " :last-updated (time/date-time 2012 1 10) :public-reset false :public-plus true}]
                                     :name "Kaleidos Team"}}))

(defn make-system []
  (component/system-map
    :db (memstore/->InMemoryDatabase initial-data)
    :api (component/using
          (api/map->API {})
          [:db])
    :routes (component/using
             (routes/map->Routes {})
             [:api])
    :server (component/using
             (server/map->HTTPServer {:opts {:port 8080}})
             [:routes])))


(defn -main [& args]
  (let [system (make-system)]
    (component/start system)))
