(ns thefinalcountapp.data.schemas
  #+clj
  (:require [schema.core :as s])
  #+cljs
  (:require [schema.core :as s :include-macros true]))


(def NewGroup
  {:name s/Str})


(def NewCounter
  {:type (s/enum :count-up
                 :streak
                 :counter)
  :text s/Str
  :value s/Int
  (s/optional-key :public-reset) s/Bool
  (s/optional-key :public-plus) s/Bool})


(def CounterUpdate
  {(s/optional-key :text) s/Str
   (s/optional-key :value) s/Int
   (s/optional-key :public-reset) s/Bool
   (s/optional-key :public-plus) s/Bool})
