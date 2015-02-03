(ns cerberus.core.api
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as middleware]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]))


(defn get-proxy-list [request component]
  {:body {:status "tested"}})

(defn create-proxy [request component]
  {:body {:status "tested"}})

(defn reset-controller [request component]
  {:body {"status" "tested"}})

(defn get-proxy-inst [request proxy-id component]
  {:body {"status" (str "tested " proxy-id)}})

(defn modify-proxy [request proxy-id component]
  {:body {"status" (str "tested " proxy-id)}})

(defn delete-proxy [request proxy-id component]
  {:body {"status" (str "tested " proxy-id)}})

(defn get-proxy-requests [request proxy-id component]
    {:body {"status" (str "tested " proxy-id)}})

(defn app-routes [component]
  (compojure.core/routes
   (GET "/proxy" [:as req] (get-proxy-list req component))
   (POST "/proxy" [:as req] (create-proxy req component))
   (DELETE "/proxy" [:as req] (reset-controller req component))
   (GET "/proxy/:proxy-id" [proxy-id :as req] (get-proxy-inst req proxy-id component))
   (PUT "/proxy/:proxy-id" [proxy-id :as req] (modify-proxy req proxy-id component))
   (DELETE "/proxy/:proxy-id" [proxy-id :as req] (delete-proxy req proxy-id component))
   (GET "/proxy/:proxy-id/requests" [proxy-id :as req] (get-proxy-requests req proxy-id component))
   (route/not-found "Resource not found")))


(defn get-app [component]
  (-> (routes (app-routes component))
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (wrap-request-logging)))

(defn run
  [options component]
  (let [options (merge {:port 8080 :join? false} options)
        app (get-app component)]
    (jetty/run-jetty app options)))


(defrecord Api [port]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [component]
    (println ";; Starting controller")
    ;; In the 'start' method, initialize this component
    ;; and start it running. For example, connect to a
    ;; database, create thread pools, or initialize shared
    ;; state.
    (let [options (merge {:port (:port component)} (:options component))
          app (app-routes component)
          srv (jetty/run-jetty app options)
          ]
      (assoc component :options options)
      (assoc component :srv srv)))

  (stop [component]
    (println ";; Stopping controller")
    ;; In the 'stop' method, shut down the running
    ;; component and release any external resources it has
    ;; acquired.
    (.stop (:srv component))
    ;; Return the component, optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    (assoc component :options nil)
    (assoc component :srv nil)))


(defn build-api [port]
  (map->Api {:port port}))

(defn start-api []
  (let [comp (map->Api {:port 8080})]
    (get-app comp)))
 
