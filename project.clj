(defproject mike/cruddy "0.0.1-SNAPSHOT"
  :description "A single-page application for browsing a movie collection."
  :url "https://github.com/mike706574/cruddy"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/timbre "4.8.0"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [environ "1.1.0"]
                 [org.clojure/clojurescript "1.9.494"]
                 [reagent "0.6.0"]
                 [reagent-forms "0.5.29"]
                 [re-frame "0.9.2"]
                 [day8.re-frame/http-fx "0.1.3"]
                 [cljs-ajax "0.5.8"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [cider/cider-nrepl "0.14.0"]
            [org.clojure/tools.nrepl "0.2.12"]
            [lein-figwheel "0.5.9"]]
  :source-paths ["src/clj"]
  :hooks [leiningen.cljsbuild]
  :profiles {:dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [;[clj-http "3.4.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.9"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :cljsbuild
                   {:builds
                    {:client
                     {:figwheel {:on-jsload "cruddy.core/run"
                                 :websocket-host "192.168.1.141"}
                      :compiler {:main "cruddy.core"
                                 :asset-path "js"
                                 :optimizations :none
                                 :closure-defines {cruddy.core/api-uri "http://192.168.1.141:8000"}
                                 :source-map true
                                 :source-map-timestamp true}}}}}
             :production {:aot :all
                          :main cruddy.server
                          :uberjar-name "cruddy.jar"
                          :cljsbuild
                          {:builds
                           {:client
                            {:compiler {:output-dir "target"
                                        :optimizations :advanced
                                        :elide-asserts true
                                        :closure-defines {cruddy.core/api-uri "https://mike-movie-server.herokuapp.com"}
                                        :pretty-print false}}}}}}
  :figwheel {:repl false
             :server-port 3500}
  :clean-targets ^{:protect false} ["resources/public/js"]
  :cljsbuild {:builds {:client {:source-paths ["src/cljs"]
                                :compiler {:output-dir "resources/public/js"
                                           :output-to "resources/public/js/client.js"}}}})
