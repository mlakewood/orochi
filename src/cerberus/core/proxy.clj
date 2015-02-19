(ns cerberus.core.proxy
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]))

(defn append-request [component incoming new-req resp]
  (let [counter (swap! (get component :counter) inc)
        log {:count counter
             :incoming incoming
             :mod new-req
             :response resp}]
    (swap! (:requests component) conj log)))

(defn handler [request component]
  (let [new-req (merge request (:backend component))
        resp (client/request new-req)]
    (append-request component request new-req resp)
    resp))

(defn app-routes [component]
  (compojure.core/routes
   (ANY "/*" [:as req] (handler req component))
   (route/not-found "Resource not found")))


(defn get-app [component]
  (-> (routes (app-routes component))
      (wrap-request-logging)))

(defrecord Proxy [requests backend options counter]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [component]
    (println ";; Starting proxy")
    ;; In the 'start' method, initialize this component
    ;; and start it running. For example, connect to a
    ;; database, create thread pools, or initialize shared
    ;; state.
    (let [backend (:backend component)
          options (merge {:port (:port component)} (:options component))
          app (app-routes component)
          srv (jetty/run-jetty app options)
          ]
      (assoc component :options options)
      (assoc component :srv srv)))

  (stop [component]
    (println ";; Stopping proxy")
    ;; In the 'stop' method, shut down the running
    ;; component and release any external resources it has
    ;; acquired.
    (.stop (:srv component))
    ;; Return the component, optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    (assoc component :options nil)
    (assoc component :srv nil)))

(defn build-proxy [requests backend port controller]
  (map->Proxy {:requests requests :backend backend :options {:port port :join? false} :counter (:request-counter controller)}))


(defn convert-json [json]
  (println json))
