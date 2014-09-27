(ns thefinalcountapp.data.store
  (:require [com.stuartsierra.component :as component]))


(defrecord Database [dbspec]
  component/Lifecycle
  (start [this]
    ; TOOD connection pooling when we have a proper DB
    (assoc this :pool dbspec))

  (stop [this]
    (dissoc this :pool)))
