(ns user
  (:require [com.stuartsierra.component :as component]
            [thefinalcountapp.core :refer [make-system]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.test :refer [run-tests]]))


(defonce system (make-system))


(defn start []
  (alter-var-root #'system component/start))


(defn stop []
  (alter-var-root #'system component/stop))


(defn reboot []
  (stop)
  (refresh)
  (start))


(defn test-all []
  (refresh)
  (run-tests 'thefinalcountapp.memory-store-test))
