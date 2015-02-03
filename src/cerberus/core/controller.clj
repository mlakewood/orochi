(ns cerberus.core.controller
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]
            [cerberus.core.proxy :refer :all]))


;; {:original-config config
;;  :proxies []
;;  }

;; (def proxy2 (map->Proxy {:backends backend :options {:join? false} :port 8081 :app-routes app-routes :requests (atom [])}))


;; (defn add-proxy [proxy-config component]
;;   (let [requests (atom [])
;;         name (get proxy-config "name")
;;         backends (:backends proxy-config)
;;         options {:join? false}
;;         port (:port proxy-config)
;;         proxy (build-proxy )])
;;   (swap! (:proxies component) conj proxy))




;; (defrecord Controller [config]
;;   ;; Implement the Lifecycle protocol
;;   component/Lifecycle

;;   (start [component]
;;     (println ";; Starting controller")
;;     ;; In the 'start' method, initialize this component
;;     ;; and start it running. For example, connect to a
;;     ;; database, create thread pools, or initialize shared
;;     ;; state.
;;     (let []))

;;   (stop [component]
;;     (println ";; Stopping controller")
;;     ;; In the 'stop' method, shut down the running
;;     ;; component and release any external resources it has
;;     ;; acquired.
;;     ;; Return the component, optionally modified. Remember that if you
;;     ;; dissoc one of a record's base fields, you get a plain map.
;; ))

;; (defn build-controller [config]
;;   (map->Controller {:original-config config :proxies (atom [])}))
