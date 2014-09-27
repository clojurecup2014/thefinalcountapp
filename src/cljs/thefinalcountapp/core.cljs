(ns thefinalcountapp.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defn main-component []
  [:h1 "The Final Count App (From CLJS)"])


(defn ^:export run []
  (reagent/render-component [main-component]
                            (.-body js/document)))
