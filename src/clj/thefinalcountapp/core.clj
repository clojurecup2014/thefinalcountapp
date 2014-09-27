(ns thefinalcountapp.core
  (:require [thefinalcountapp.http.server :as server]
            [thefinalcountapp.http.api :as api]
            [thefinalcountapp.http.routes :as routes]
            [thefinalcountapp.db :as db]
            [com.stuartsierra.component :as component]))


(defn make-system []
  (component/system-map
    :db (db/map->Database {:dbspec {}})
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
