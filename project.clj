(defproject thefinalcountapp "0.1.0-SNAPSHOT"
  :description "The Final Count App is an application for sharing your personal records with your team or friends. Have you ever been two full days without breaking your local git repository? Now you can brag about it!"
  :url "http://thefinalcountapp.clojurecup.com"
  :license {:name "GNU Affero General Public License"
            :url "http://www.gnu.org/licenses/agpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha2"]
                 [org.clojure/clojurescript "0.0-2322"]

                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]

                 [http-kit "2.1.19"]
                 [compojure "1.1.9"]
                 [ring "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [com.cognitect/transit-clj "0.8.247"]
                 [com.cognitect/transit-cljs "0.8.188"]

                 [com.stuartsierra/component "0.2.2"]
                 [liberator "0.12.2"]
                 [io.clojure/liberator-transit "0.3.0"]

                 [cats "0.2.0-SNAPSHOT" :exclusions [org.clojure/clojure]]

                 [reagent "0.4.2"]

                 [prismatic/schema "0.3.0"]]
  :main thefinalcountapp.core
  :plugins [[lein-cljsbuild "1.0.3"]
            [com.keminglabs/cljx "0.4.0" :exclusions [org.clojure/clojure]]]
  :source-paths ["src/clj"]
  :cljsbuild
  {:builds
   {:client {:source-paths ["src/cljs"]
             :compiler
             {;; :preamble ["reagent/react.js"]
              :output-dir "resources/public/js"
              :output-to "resources/public/js/thefinalcountapp.js"
              :pretty-print true}}}}
  :cljx
  {:builds [{:source-paths ["src/cljx"]
             :output-path "src/clj"
             :rules :clj}
            {:source-paths ["src/cljx"]
             :output-path "src/cljs"
             :rules :cljs}]}
  :hooks [cljx.hooks])
