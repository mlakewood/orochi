(ns orochi.core.proxy
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [orochi.core.serializer :as serializer]
            [clojure.pprint :refer [pprint]]
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

  (start [comp]
    (println ";; Starting proxy")
    ;; In the 'start' method, initialize this component
    ;; and start it running. For example, connect to a
    ;; database, create thread pools, or initialize shared
    ;; state.
    (let [backend (:backend comp)
          options (merge {:port (:port comp)} (:options comp))
          app (app-routes comp)
          srv (jetty/run-jetty app options)
          started-command (component/start (:command comp))
          comp (assoc comp :command started-command)
          comp (assoc comp :options options)
          comp (assoc comp :srv srv)]
      comp))

  (stop [comp]
    (println ";; Stopping proxy")
    ;; In the 'stop' method, shut down the running
    ;; component and release any external resources it has
    ;; acquired.
    (.stop (:srv comp))
    ;; Return the component, optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    (let [comp (assoc comp :options nil)
          comp (assoc comp :srv nil)
          comp (assoc comp :command (component/stop (:command comp)))]
      comp))
  serializer/Serialize
  (->json [this]
    (let [result {:requests @(:requests this)
                  :backend (:backend this)
                  :options (:options this)
                  :command (serializer/->json (:command this))}]
      result)))

(defn build-proxy [requests backend port controller command]
  (map->Proxy {:requests requests
               :backend backend
               :options {:port port :join? false}
               :counter (:request-counter controller)
               :command command}))