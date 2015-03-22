(ns orochi.core.controller
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [orochi.core.proxy :refer [build-proxy]]
            [orochi.core.serializer :as serializer]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :refer [generate-string]]
            ))

(defn add-proxy [controller name backends port command]
  (let [proxy (build-proxy name (atom []) backends port controller command)
        proxy (component/start proxy)
        _ (swap! (:proxies controller) assoc name proxy)
        ]
    controller))

(defn get-proxy [controller name]
  (get @(:proxies controller) name))

(defn stop-proxy [controller proxy-name]
  (if (:srv (get @(:proxies controller) proxy-name))
    (swap! (:proxies controller) assoc proxy-name (component/stop (get @(:proxies controller) proxy-name))))
  controller)

(defn stop-all-proxies [controller]
  (let [proxy-names (keys @(:proxies controller))]
    (loop [n (vec proxy-names)
           c controller]
      (if (empty? n)
        c
        (recur (rest n) (stop-proxy c (first n)))))))

(defn clear-proxies [controller]
  (let [cont (stop-all-proxies controller)
        cont (assoc cont :proxies (atom {}))]
    cont))


(defrecord Controller [config proxies request-counter]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (println ";; Starting controller")
    ;; In the 'start' method, initialize this component
    ;; and start it running. For example, connect to a
    ;; database, create thread pools, or initialize shared
    ;; state.
    this)

  (stop [this]
    (println ";; Stopping controller")
    ;; In the 'stop' method, shut down the running
    ;; component and release any external resources it has
    ;; acquired.
    ;; Return the component, optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    (let [this (stop-all-proxies this)]
      this))
  
  serializer/Serialize
  (->json [this]
    (let [proxy-names (keys @(:proxies this))
          proxy-result (map #(assoc (serializer/->json (get @(:proxies this) %1)) :name  %1) proxy-names)]
      (generate-string proxy-result))))

(defn build-controller [config]
  (map->Controller {:config config :proxies (atom {}) :request-counter (atom 0)}))


