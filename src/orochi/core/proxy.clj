(ns orochi.core.proxy
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [orochi.core.serializer :as serializer]
            [clojure.pprint :refer [pprint]]
            [orochi.proxies.pass-through-proxy :refer [proxy-request]]
            [orochi.proxies.web-hook :refer [web-hook]]
            [orochi.proxies.mock-proxy :refer [mock-request]]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]))

(defmulti handler (fn [comp request] (:type (:backend comp))))

(defmethod handler "proxy" [component request]
  (proxy-request component request))

(defmethod handler "web-hook" [component request]
  (web-hook component request))

(defmethod handler "mock-request" [component request]
  (mock-request component request))

(defmethod handler :default [componet request]
 (throw (IllegalArgumentException. 
          (str "No backend type set"))))


(defn app-routes [component]
  (compojure.core/routes
   (ANY "/*" [:as req] (handler component req))
   (route/not-found "Resource not found")))

(defn fetch-body [body]
  (if (instance? org.eclipse.jetty.server.HttpInput body)
    (try
      (slurp body)
      ;; TODO Why could this have an Exception?
      (catch java.io.IOException e ""))
    body))


(defn serialize-actions [actions]
  (if (seq actions)
    (let [req (update-in actions [:incoming :body] #(fetch-body %1))
          req (update-in req [:mod :body] #(fetch-body %1))
          req (update-in req [:response :body] #(fetch-body %1))]      
      req)
    []))

(defn get-app [component]
  (routes (app-routes component)))

(defrecord Proxy [name actions backend options counter]
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
    (let [result {:actions (map serialize-actions @(:actions this)) 
                  :name (:name this)
                  :backend (:backend this)
                  :options (:options this)
                  :command (serializer/->json (:command this))}]
      result)))

(defn build-proxy [name actions backend port controller command]
  (map->Proxy {:name name
               :actions actions
               :backend backend
               :options {:port port :join? false}
               :counter (:request-counter controller)
               :command command}))
