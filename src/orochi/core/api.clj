(ns orochi.core.api
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as middleware]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [clojure.pprint :refer [pprint]]
            [com.stuartsierra.component :as component]
            [orochi.core.serializer :as serializer]
            [orochi.core.controller :refer [build-controller add-proxy]]
            [orochi.core.commands :refer [value->command]]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]))

(defn get-proxy-list [request comp]
  (let [cont (:cont @(:controller comp))]
      {:body (serializer/->json cont)}))

(defn create-proxy [request component]
  (let [body (parse-string (slurp (:body request)) true)
        name (:name body)
        backends (:backend body)
        port (:front-port body)
        command (:command body)
        new-com (value->command command)
        _ (swap! (:controller component) assoc :cont (add-proxy (:cont @(:controller component)) name backends port new-com))
        ])
  {:body (:body request)})


(defn reset-controller [request comp]
  (let [stopped (component/stop (:cont @(:controller comp)))]
    {:body (serializer/->json stopped)}))

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

(defrecord Api [port join]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [comp]
    (println ";; Starting api")
    ;; In the 'start' method, initialize t
    ;; and start it running. For example, connect to a
    ;; database, create thread pools, or initialize shared
    ;; state.
    (let [comp (assoc comp :controller (atom {:cont (component/start (build-controller {}))})) 
          options (:options comp)
          app (get-app comp)
          srv (jetty/run-jetty app options)
          comp (assoc comp :options options)
          comp (assoc comp :app app)
          comp (assoc comp :srv srv)]
      comp))

  (stop [comp]
    (println ";; Stopping api")
    ;; In the 'stop' method, shut down the running
    ;; and release any external resources it has
    ;; acquired.
    (.stop (:srv comp))
    ;; Return , optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    (let [comp (assoc comp :options nil)
          comp (assoc comp :app nil)
          comp (assoc comp :srv nil)
          comp (assoc comp :controller (component/stop (:cont @(:controller comp))))]
      comp)))


(defn build-api [ring-options]
  (map->Api {:options ring-options}))

(defn start-api [port]
  (let [comp (build-api {:port port :join? false})]
    (component/start comp)))
 
(defn -main [& args]
  (if-let [port (first args)]
    (start-api (Integer. port))
    (start-api 8081)))
