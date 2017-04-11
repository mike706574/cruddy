(ns cruddy.server
  (:gen-class :main true)
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [resource-response file-response not-found]]
            [ring.middleware.resource :refer [wrap-resource]]
            [taoensso.timbre :as log]))

(defn root
  [request]
  (if (= (:uri request) "/")
    (resource-response "index.html" {:root "public"})
    (resource-response "404.html" {:root "public"})))

(def handler
  (-> root
      (wrap-resource "public")))

(defn- already-started
  [{:keys [id port] :as service}]
  (log/info (str "Service " id " already started on port " port "."))
  service)

(defn- start-service
  [{:keys [id port handler] :as service} handler]
  (log/info (str "Starting " id " on port " port "..."))
  (try
    (let [server (jetty/run-jetty handler {:port port
                                           :join? false})]
      (log/info (str "Finished starting."))
      (assoc service :server server))
    (catch java.net.BindException e
      (throw (ex-info (str "Port " port " is already in use.") {:id id
                                                                :port port})))))

(defn- stop-service
  [{:keys [id port server] :as service}]
  (log/info (str "Stopping " id " on port " port "..."))
  (.stop server)
  (dissoc service :server))

(defn- already-stopped
  [{:keys [id] :as service}]
  (log/info (str id " already stopped."))
  service)

(defrecord JettyService [id port handler server]
  component/Lifecycle
  (start [this]
    (if server
      (already-started this)
      (start-service this handler)))
  (stop [this]
    (if server
      (stop-service this)
      (already-stopped this))))

(defn jetty-service
  [{:keys [port] :as config} handler]
  {:pre [(integer? port)
         (> port 0)]}
  (component/using (map->JettyService (assoc config :handler handler)) []))

(defn system
  [config]
  {:app (jetty-service config handler)})

(defn -main
  [& [port]]
  (component/start-system
   (system {:id "cruddy"
            :port (Integer. (or port (env :port) 5001))})))
