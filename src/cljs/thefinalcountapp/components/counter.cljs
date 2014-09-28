(ns thefinalcountapp.components.counter
  (:require [goog.string :as gstring]
            [goog.i18n.DateTimeFormat]
            [thefinalcountapp.time :as time]))


(def banner-colors
  {:red "#e34a3f"
   :pink "#f67bab"
   :blue "#29ACB8"
   :purple "#523C8D"
   :yellow "#f4c85c"})

(def banner-colors-dark
  {:red "#d52b1f"
   :pink "#f55794"
   :blue "#0095A3"
   :purple "#412f72"
   :yellow "#E0B957"})

(def banner-colors-darker
  {:red "#ae2319"
   :pink "#F1005E"
   :blue "#0E7A85"
   :purple "#291E46"
   :yellow "#C2A04D"})

(defn counter-type->text [type]
  (cond
   (= type :count-up) "DAYS WITHOUT"
   (= type :streak) "DAYS DOING"
   :else "TIMES"))

(defn counter->count [counter]
  (case (:type counter)
    :counter (:value counter)
    :count-up (time/days-since (:last-updated counter))
    :streak (time/days-since (:last-updated counter))))

(defn format-date [date]
  (.format (goog.i18n.DateTimeFormat. (.-MEDIUM_DATETIME goog.i18n.DateTimeFormat.Format))
           (js/Date. date)))

(defn button-share []
  [:g {:transform "translate(64,-10)"}
   [:rect {:width "38" :height "36" :rx "7.2934284" :ry "7.2934284" :x "363.26935" :y "767.5" :style #js {"fill" "#128995" "fill-opacity" "1" "stroke" "none" "display" "inline"}}]
   [:rect {:width "38" :height "36" :rx "7.2934284" :ry "7.2934284" :x "363.26935" :y "763.5" :style #js {"fill" "#18b3c2" "fill-opacity" "1" "stroke" "none" "display" "inline"}}]
   [:g {:transform "matrix(0.0420943,0,0,0.0420943,336.67001,761.75892)" :style #js {"fill" "#ffffff" "display" "inline"}}
    [:path {:d "M 875.34697,723.01473 C 853.04404,717.16809 835.85702,699.95341 829.31972,676.91342 827.58726,670.80755 827.5,660.81316 827.5,468.5 l 0,-202 2.72441,-8 c 5.90006,-17.32502 17.88441,-31.28761 33.12971,-38.59839 C 878.26388,212.75174 873.02117,213 1009.102,213 l 121.9261,0 -0.264,31.75 -0.2641,31.75 -115.0443,0.5 c -107.73625,0.46824 -115.21529,0.61205 -117.73607,2.26393 -1.48048,0.97016 -3.48554,2.97538 -4.4557,4.45605 -1.66897,2.54719 -1.76393,12.51977 -1.76393,185.23607 0,170.17167 0.11562,182.75509 1.70595,185.65911 3.78552,6.91255 -11.31593,6.42887 191.78885,6.14281 l 183.159,-0.25797 3.4231,-3.73531 3.4231,-3.73532 0,-75.56434 c 0,-63.70539 0.2251,-75.98814 1.4342,-78.26469 0.7888,-1.48519 15.1888,-16.42257 32,-33.19419 L 1339,435.5123 l 0,117.53375 c 0,130.22572 0.3802,122.40585 -6.659,136.95395 -5.0432,10.42283 -17.704,23.00919 -28.4701,28.30275 -14.4553,7.10746 0.2761,6.6666 -221.3709,6.62482 -192.28445,-0.0362 -200.27595,-0.11003 -207.15303,-1.91284 z m 80.51244,-194.50624 c -0.79573,-4.13191 1.22834,-24.63785 3.70234,-37.50849 15.3283,-79.74333 73.20835,-145.47114 150.23715,-170.60722 16.8102,-5.48551 34.5512,-8.9416 53.7011,-10.46141 8.25,-0.65475 15.2774,-1.62401 15.6165,-2.1539 0.3391,-0.52989 0.6669,-16.48125 0.7285,-35.44746 l 0.112,-34.48401 2.6569,-5.92048 c 3.0706,-6.84218 9.3272,-13.10341 16.2104,-16.22245 6.7853,-3.07466 18.8374,-2.95215 25.6757,0.26099 3.9613,1.8613 15.8416,13.1539 57.188,54.35879 60.0765,59.87097 57.8911,57.09421 57.1071,72.56055 -0.3209,6.32938 -1.0305,9.76401 -2.6733,12.9392 -1.4583,2.8184 -20.2248,22.33875 -53.9292,56.09524 -57.3587,57.44748 -55.939,56.27995 -69.1926,56.90401 -10.0884,0.47503 -17.2656,-2.18265 -23.9524,-8.86947 -8.7721,-8.77213 -9.088,-10.50699 -9.2039,-50.54956 -0.08,-27.81315 -0.3613,-34.5875 -1.4715,-35.50885 -1.0247,-0.85043 -4.8791,-0.85299 -15.2314,-0.0101 -39.8147,3.24178 -74.6477,15.04795 -106.5444,36.11176 -13.6074,8.98607 -23.4605,17.06359 -35.4988,29.10192 -17.0961,17.09606 -25.4237,28.48964 -49.10401,67.18265 -6.32148,10.32912 -12.45376,19.99199 -13.62731,21.47305 -1.94203,2.45093 -2.16724,2.51877 -2.50687,0.75523 z"} :style #js {"fill" "#ffffff"}]]])


(defn button-reset [cb]
  [:g {:transform "translate(-20.5,-69.5)"
       :on-click cb
       :style #js {"cursor" "pointer"}}
   [:rect {:width "80.5" :height "36" :rx "7.2934284" :ry "7.2934284" :x "826.51935" :y "828" :style #js {"fill" "#d52b1f" "fill-opacity" "1" "stroke" "none"}}]
   [:rect {:width "80.5" :height "36" :rx "7.2934284" :ry "7.2934284" :x "826.51935" :y "824" :style #js {"fill" "#e34a3f" "fill-opacity" "1" "stroke" "none"}}]
   [:text {:x "840.86035" :y "849.90076" :style #js {"font-size" "37.50714874px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#ffffff" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
    [:tspan {:x "840.86035" :y "849.90076" :style #js {"font-size" "20px" "font-style" "normal" "font-variant" "normal" "font-weight" "500" "font-stretch" "normal" "text-align" "start" "line-height" "125%" "writing-mode" "lr-tb" "text-anchor" "start" "fill" "#ffffff" "fill-opacity" "1" "font-family" "Ubuntu"}} "Reset"]
    ]
   ])

(defn button-plus [cb]
  [:g {:transform "translate(-115,-69.5)"
       :on-click cb
       :style #js {"cursor" "pointer"}}
   [:rect {:width "80.5" :height "36" :rx "7.2934284" :ry "7.2934284" :x "826.51935" :y "828" :style #js {"fill" "#d52b1f" "fill-opacity" "1" "stroke" "none"}}]
   [:rect {:width "80.5" :height "36" :rx "7.2934284" :ry "7.2934284" :x "826.51935" :y "824" :style #js {"fill" "#e34a3f" "fill-opacity" "1" "stroke" "none"}}]
   [:text {:x "840.86035" :y "849.90076" :style #js {"font-size" "37.50714874px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#ffffff" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
    [:tspan {:x "855" :y "849.90076" :style #js {"font-size" "20px" "font-style" "normal" "font-variant" "normal" "font-weight" "500" "font-stretch" "normal" "text-align" "start" "line-height" "125%" "writing-mode" "lr-tb" "text-anchor" "start" "fill" "#ffffff" "fill-opacity" "1" "font-family" "Ubuntu"}} "+1"]
    ]
   ])


(defn render-counter [counter-data]
  (let [title (counter-type->text (:type counter-data))
        text-line1 (subs (:text counter-data) 0 20)
        text-line2 (subs (:text counter-data) 20)
        str-val (gstring/format "%03d" (counter->count counter-data))
        counter-a (nth str-val 0)
        counter-b (nth str-val 1)
        counter-c (nth str-val 2)
        color-light ((:color counter-data) banner-colors)
        color-dark ((:color counter-data) banner-colors-dark)
        color-darker ((:color counter-data) banner-colors-darker)
        reset-text (str "Last reset: " (format-date (:last-updated counter-data)) )]
    (list
     [:rect {:width "482.69235" :height "487.99667" :rx "7.2934284" :ry "7.2934284" :x "415.93054" :y "323.10889" :style #js {"fill" "#989898" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "482.69235" :height "487.99667" :rx "7.2934284" :ry "7.2934284" :x "415.93054" :y "317.48282" :style #js {"fill" "#ffffff" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "425.00797" :height "220.79196" :rx "7.2934284" :ry "7.2934284" :x "444.97101" :y "492.94446" :style #js {"fill" "#d7d7d7" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "125.88039" :height "184.32481" :rx "7.0647144" :ry "7.0647144" :x "458.09851" :y "511.69803" :style #js {"fill" "#989898" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "125.88039" :height "184.32481" :rx "7.0647144" :ry "7.0647144" :x "594.33948" :y "511.69803" :style #js {"fill" "#989898" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "125.88039" :height "184.32481" :rx "7.0647144" :ry "7.0647144" :x "730.58063" :y "511.69803" :style #js {"fill" "#989898" "fill-opacity" "1" "stroke" "none"}}]
     [:g {:transform "matrix(0.93767868,0,0,0.93767868,-344.04539,65.552028)"}
      [:path {:d "m 1347.125,244.1875 -120,0 0,73 120,0 -20,-36.5 z"    :style #js {"fill" color-dark "fill-opacity" "1" "stroke" "none"}}]
      [:path {:d "m 1227.2484,317.0625 41.75,-10.625 -41.875,-0.3125 z" :style #js {"fill" color-darker "fill-opacity" "1" "stroke" "none"}}]
      [:path {:d "m 784.87341,244 120,0 0,73 -120,0 20,-36.5 z"         :style #js {"fill" color-dark "fill-opacity" "1" "stroke" "none"}}]
      [:path {:d "M 904.75,316.875 863,306.25 l 41.875,-0.3125 z"       :style #js {"fill" color-darker "fill-opacity" "1" "stroke" "none"}}]
      [:rect {:width "406" :height "73" :x "862.87341" :y "233.5" :style #js {"fill" color-light "fill-opacity" "1" "stroke" "none"}}]
      [:text {:x "952.60876" :y "288.008" :style #js {"font-size" "40px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#000000" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
       [:tspan {:x "1070" :y "288.008" :text-anchor "middle" :style #js {"text-align" "center" "font-size" "48px" "font-style" "normal" "font-variant" "normal" "font-weight" "normal" "font-stretch" "normal" "fill" "#ffffff" "font-family" "Ubuntu"}}
        title]
       ]
      ]

     [:rect {:width "125.88039" :height "184.32481" :rx "7.0647144" :ry "7.0647144" :x "458.09851" :y "506.07193" :style #js {"fill" "#ffffff" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "125.88039" :height "184.32481" :rx "7.0647144" :ry "7.0647144" :x "594.33948" :y "506.07193" :style #js {"fill" "#ffffff" "fill-opacity" "1" "stroke" "none"}}]
     [:rect {:width "125.88039" :height "184.32481" :rx "7.0647144" :ry "7.0647144" :x "730.58063" :y "506.07193" :style #js {"fill" "#ffffff" "fill-opacity" "1" "stroke" "none"}}]

     [:text {:x "470.02893" :y "663.30927" :style #js {"font-size" "196.89212036px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#412f72" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
      [:tspan {:x "470.02893" :y "663.30927" :style #js {"font-size" "187.53573608px" "fill" "#412f72" "fill-opacity" "1" "font-family" "ubuntu"}} counter-a]]

     [:text {:x "603.26941" :y "663.30927" :style #js {"font-size" "196.89212036px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#412f72" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
      [:tspan {:x "603.26941" :y "663.30927" :style #js {"font-size" "187.53573608px" "fill" "#412f72" "fill-opacity" "1" "font-family" "ubuntu"}} counter-b]]

     [:text {:x "741.38593" :y "663.30927" :style #js {"font-size" "196.89212036px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#412f72" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
      [:tspan {:x "741.38593" :y "663.30927" :style #js {"font-size" "187.53573608px" "fill" "#412f72" "fill-opacity" "1" "font-family" "ubuntu"}} counter-c]]

     [:rect {:width "416.38846" :height "3.6467142" :rx "3.3151948" :ry "1.8233571" :x "450.93835" :y "418.66232" :style #js {"fill" "#f5ebd4" "fill-opacity" "1" "stroke" "none"}}]
     [:text {:x "486.92908" :y "416.90073" :style #js {"font-size" "37.50714874px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#412f72" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}
      [:tspan {:x "660" :y "416.90073" :style #js {"font-size" "33.75643158px" "font-style" "normal" "font-variant" "normal" "font-weight" "normal" "font-stretch" "normal" "fill" "#412f72" "fill-opacity" "1" "font-family" "Ubuntu" "text-anchor" "middle"}} text-line1]
      [:tspan {:x "660" :y "459.90073" :style #js {"font-size" "33.75643158px" "font-style" "normal" "font-variant" "normal" "font-weight" "normal" "font-stretch" "normal" "fill" "#412f72" "fill-opacity" "1" "font-family" "Ubuntu" "text-anchor" "middle"}} text-line2]]
     [:rect {:width "416.38846" :height "3.6467142" :rx "3.3151948" :ry "1.8233571" :x "450.93835" :y "465.54626" :style #js {"fill" "#f5ebd4" "fill-opacity" "1" "stroke" "none"}}]
     [:text {:x "515.60193" :y "742.90076" :style #js {"font-size" "37.50714874px" "font-style" "normal" "font-weight" "normal" "line-height" "125%" "letter-spacing" "0px" "word-spacing" "0px" "fill" "#18b3c2" "fill-opacity" "1" "stroke" "none" "font-family" "Sans"}}>
      [:tspan {:x "658" :y "742.90076" :text-anchor "middle" :style #js {"font-size" "18px" "font-style" "italic" "font-variant" "normal" "font-weight" "300" "font-stretch" "normal" "line-height" "125%" "writing-mode" "lr-tb" "fill" "#18b3c2" "fill-opacity" "1" "font-family" "Ubuntu"}} reset-text]]
     )
    )
  )
(defn counter [counter-data]
  [:g {:transform "translate(-380, -280)" :style #js {"display" "inline"}}
      (render-counter counter-data)])

(defn counter-with-buttons [counter-data cb-reset cb-add]
  [:g {:transform "translate(-380, -280)" :style #js {"display" "inline"}}
   (concat (render-counter counter-data)
           (list [button-reset #(cb-reset (:id counter-data))])
           (list [(if (= (:type counter-data) :counter) button-plus :g ) #(cb-add (:id counter-data))]))
   ])
