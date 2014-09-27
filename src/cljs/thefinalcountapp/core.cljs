(ns thefinalcountapp.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [thefinalcountapp.http :as http]
            [thefinalcountapp.components.counter :refer [counter]]
            [cljs.core.async :refer [<!]]))

(def state (atom {:group "kaleidos-team"
                  :counters []}))

;; (defn main-component []
;;   [:div.content
;;    [counter]
;;    [:ul (for [item (:counters @state)] ^{:key item} [:li "Counter: " (:text item)])]
;;    [:p (str ">>" (count (:counters @state)))]])

;(defn main-component []
;  [:div.content
;   [:svg {:width "100%" :height "100%"}
;    [:g {:transform "translate(680, 300)"}
;     [:g {:transform "scale(1.0, 1.0)"} [counter]]]]])

(defn main-component []
  [:div.content
   [:svg {:width "100%" :height "100%"}
    [counter]]])


(defn ^:export run []
  (go (let [response (<! (http/get "/api/counters/kaleidos-team"))]
        (.log js/console (str response))
        (swap! state #(assoc % :counters (:data response)))))
  (reagent/render-component [main-component]
                            (.-body js/document)))
