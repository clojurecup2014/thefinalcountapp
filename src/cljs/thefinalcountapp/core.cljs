(ns thefinalcountapp.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [thefinalcountapp.http :as http]
            [cljs.core.async :refer [<!]]))

(defn main-component []
  [:div.content
   [:h1.logo-title "The Final Count App (From CLJS)"]])


(defn ^:export run []
  (go (let [response (<! (http/get "/api/counters/kaleidos-team"))]
        (.log js/console (-> response :data first :text))))
  (reagent/render-component [main-component]
                            (.-body js/document)))
