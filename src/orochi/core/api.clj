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
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]])
  (:gen-class))

(defn get-index []
  {:body [{:method "GET" :url "/" :desc "Get all possible resources"}
           {:method "GET" :url "/proxy" :desc "Get all Proxies"}
           {:method "POST" :url "/proxy" :desc "Create a new proxy"}
           {:method "DELETE" :url "/proxy" :desc "Shutdown and remove All Proxies"}
           {:method "GET" :url "/proxy/<proxy-name>" :desc "Get the proxy instance resource"}
           {:method "DELETE" :url "/proxy/<proxy-name>" :desc "Shutdown and remove the proxy instance"}]})

(defn get-proxy-list [request comp]
  (let [cont (:cont @(:controller comp))]
      {:body (serializer/->json cont)}))

(defn create-proxy [request component]
  (let [body (parse-string (slurp (:body request)) true)
        name (:name body)
        backends (:backend body)
        port (:front-port body)
        command (:command body)
        new-com (value->command command)]
    (swap! (:controller component) assoc :cont (add-proxy (:cont @(:controller component)) name backends port new-com)))
  {:body (:body request)})


(defn reset-controller [request comp]
  (let [stopped (component/stop (:cont @(:controller comp)))]
    {:body (serializer/->json stopped)}))

(defn get-proxy-inst [request proxy-id comp]
  (let [proxies @(:proxies (:cont @(:controller comp)))
        proxy (get proxies proxy-id)
        proxy-result (if (nil? proxy)
                       {:status 404 :body {:error (str "Proxy " proxy-id " does not exist")}}
                       {:body (serializer/->json (assoc proxy :name proxy-id))})]
    proxy-result))

(defn delete-proxy [request proxy-id component]
  {:body {"status" (str "tested " proxy-id)}})

(defn app-routes [component]
  (compojure.core/routes
   (GET "/" [:as req] (get-index)) 
   (GET "/proxy" [:as req] (get-proxy-list req component))
   (POST "/proxy" [:as req] (create-proxy req component))
   (DELETE "/proxy" [:as req] (reset-controller req component))
   (GET "/proxy/:proxy-id" [proxy-id :as req] (get-proxy-inst req proxy-id component))
   (DELETE "/proxy/:proxy-id" [proxy-id :as req] (delete-proxy req proxy-id component))
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
