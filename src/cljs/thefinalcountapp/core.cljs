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
  [:div
   [:div
    [:img {:class "logo" :src "/images/logo.png" :alt "The Final Count App"}]
    [:div {:class "content-buttons"}
     [:a {:class "group" :href "#"} "Kaleidos Team"]
     [:a {:class "add-btn" :href "#"} "+"]]]
   [:div#content
    [:svg {:width "100%" :height "100%"}
     [:g {:transform "translate(350, 120) scale(0.8, 0.8)"} [counter "DÍAS SIN 1" "que nos pierda la boca" "0" "0" "1" "Último reset 18/09/2014 a las 9:00h" "0.4"]]
     [:g {:transform "translate(1080, 120) scale(0.8, 0.8)"} [counter "DÍAS SIN 2" "que nos pierda la boca" "0" "0" "3" "Último reset 18/09/2014 a las 9:00h" "0.4"]]
     [:g {:transform "translate(640, 80)"} [counter "DÍAS SIN 3" "que nos pierda la boca" "0" "0" "2" "Último reset 18/09/2014 a las 9:00h" "1.0"]]
     ]]])


(defn ^:export run []
  (go (let [response (<! (http/get "/api/counters/kaleidos-team"))]
        (.log js/console (str response))
        (swap! state #(assoc % :counters (:data response)))))
  (reagent/render-component [main-component]
                            (.-body js/document)))
