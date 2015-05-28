(ns orochi.test.core.dummy-api
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as middleware]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]))

(def record-request (atom []))

(defn get-handler [request]
  (swap! record-request conj request)
  {:body {:status "tested"}})


(defroutes dummy-routes
  (ANY "/*" [:as req] (get-handler req)))

(def dummy-app
  (-> (routes dummy-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (wrap-request-logging)))

(defn start-dummy-app [port]
  (let [options {:port port :join? false}]
    (jetty/run-jetty dummy-app options)))
