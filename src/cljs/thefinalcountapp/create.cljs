(ns thefinalcountapp.create
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [thefinalcountapp.http :as http]
            [thefinalcountapp.components.counter :refer [banner-colors counter]]
            [cljs.core.async :refer [<!]]))

(def current-counter (atom {:type :count-up
                            :color :red
                            :value 0
                            :text "text"
                            :last-updated (js/Date.)
                            :public-reset true}))

(defn header []
  [:div.head
   [:div.wrapper
    [:div.create-title "CREATE IT!"]
    [:div.preview-title "PREVIEW"]]])

(defn color-picker [color]
  [:div {:class "color-picker"
         :on-click #(swap! current-counter assoc :color color)
         :style #js {"background-color" (color banner-colors)}}])

(defn counter-form []
  [:form {:on-submit (fn [e] (go (<! (http/post "/api/counters/kaleidos-team" @current-counter)) (set! (.-location js/window) "/")) false)}
   [:select {:name "type" :on-change #(swap! current-counter assoc :type (-> % .-target .-value keyword))}
    [:option {:value "count-up"} "DAYS WITHOUT"]
    [:option {:value "streak"} "DAYS DOING"]
    [:option {:value "counter"} "TIMES"]]
   [:input {:type "text" :name "text" :placeholder "Introduce text" :on-change #(swap! current-counter assoc :text (-> % .-target .-value))}]
   [:div {:class "reset-enabled"}
    [:span "Reset enabled"]
    [:input {:type "checkbox" :name "public-reset"}]]
   [:div {:class "color-picker-panel"}
    [:input {:type "hidden" :name "color"}]
    [:span "Counter color"]
    [:div {:class "colors-panel"}
     [color-picker :red]
     [color-picker :pink]
     [color-picker :blue]
     [color-picker :purple]
     [color-picker :yellow]]]
   [:button {:type "submit"} "Create counter"]])

(defn main-component []
  [:div
   [header]
   [counter-form]
   [:svg {:width "700" :height "700" :style #js {"position" "absolute" "right" "115px"}}
    [:g {:transform "scale(1.2, 1.2)"}
     [counter @current-counter]]]])

(defn ^:export run []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "main"))))
