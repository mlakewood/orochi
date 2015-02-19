(ns cerberus.core.controller
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [cerberus.core.proxy :refer [build-proxy]]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]
            ))


(defrecord Controller [config proxies request-counter]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [component]
    (println ";; Starting controller")
    ;; In the 'start' method, initialize this component
    ;; and start it running. For example, connect to a
    ;; database, create thread pools, or initialize shared
    ;; state.
    component)

  (stop [component]
    (println ";; Stopping controller")
    ;; In the 'stop' method, shut down the running
    ;; component and release any external resources it has
    ;; acquired.
    ;; Return the component, optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    component))

(defn build-controller [config]
  (map->Controller {:config config :proxies (atom {}) :request-counter (atom 0)}))

(defn add-proxy [controller name backends port]
  (let [proxy (build-proxy (atom []) backends port controller)
        _ (swap! (:proxies controller) assoc name proxy)
        ]
    controller))

(defn get-proxy [controller name]
  (get @(:proxies controller) name))

(defn modify-proxy [controller name proxy]
  (let [_ (swap! (:proxies controller) assoc name proxy)]
    controller))
