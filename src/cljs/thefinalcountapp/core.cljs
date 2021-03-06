(ns thefinalcountapp.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [thefinalcountapp.http :as http]
            [thefinalcountapp.time :as time]
            [thefinalcountapp.components.counter :refer [counter counter-with-buttons]]
            [thefinalcountapp.state :refer [state index-of]]
            [thefinalcountapp.events :as events]
            [cljs.core.async :as async :refer [<!]]))

(defn reset-counter [state id]
  (let [idx-counter (index-of (:counters state) id)]
    (http/post (str "/api/counters/kaleidos-team/" id "/reset") {})
    (-> state
        (update-in [:counters idx-counter :last-updated] (fn [_] (js/Date.)))
        (update-in [:counters idx-counter :value] (fn [_] 0)))))

(defn reset-date [state id]
  (let [idx-counter (index-of (:counters state) id)]
    (http/post (str "/api/counters/kaleidos-team/" id "/reset") {})
    (update-in state [:counters idx-counter :last-updated] (fn [_] (js/Date.)))))

(defn increment-counter [state id]
  (let [idx-counter (index-of (:counters state) id)]
    (http/post (str "/api/counters/kaleidos-team/" id "/increment") {})
    (-> state
        (update-in [:counters idx-counter :last-updated] (fn [_] (js/Date.)))
        (update-in [:counters idx-counter :value] inc))))

(defn counter-list []
  (if (>= (count (:counters @state)) 3)
    (let [idx (:displaying @state)
          center-counter (nth (:counters @state) idx)
          left-idx (if (< (dec idx) 0) (dec (count (:counters @state))) (dec idx))
          left-counter (nth (:counters @state) left-idx)
          right-idx (if (> (inc idx) (dec (count (:counters @state)))) 0 (inc idx))
          right-counter (nth (:counters @state) right-idx)]
      [:g {:transform "translate(-350, -100) scale(1.4, 1.4)"}
       [:g {:transform "translate(350, 120) scale(0.8, 0.8)" :style #js {"opacity" "0.4"}}
        [counter left-counter]]
       [:g {:transform "translate(1080, 120) scale(0.8, 0.8)" :style #js {"opacity" "0.4"}}
        [counter right-counter]]
       [:g {:transform "translate(640, 80)" :style #js {"opacity" "1.0"}}
        (if (= (:type center-counter) :counter)
          [counter-with-buttons center-counter #(swap! state reset-counter %) #(swap! state increment-counter %)]
          [counter-with-buttons center-counter #(swap! state reset-date %) #()])]])))

(defn main-component []
  [:div
   [:div
    [:img {:class "logo" :src "/images/logo.svg" :alt "The Final Count App"}]
    [:div {:class "content-buttons"}
     [:a {:class "group" :href "http://www.kaleidos.net?language=en" :target "_blank"} "Kaleidos Team"]
     [:a {:class "add-btn" :href "/create.html"} "+"]]]
   [:div#content
    [:svg {:width "100%" :height "100%"}
     [counter-list]
     ]]])

(defn update-timer [state]
  (let [old (:displaying state)
        size (-> state :counters count)
        new (if (< (inc old) size) (inc old) 0)]
    (assoc state :displaying new)))

(defn update-counters []
  (js/setInterval (fn []
                   (swap! state update-timer)) 3000))

(defn ^:export run []
  (go (let [response (<! (http/get "/api/counters/kaleidos-team"))]
        (swap! state #(assoc % :counters (-> response :data :counters)))))

  (events/start-event-system)
  (update-counters)
  (reagent/render-component [main-component]
                            (. js/document (getElementById "main"))))
