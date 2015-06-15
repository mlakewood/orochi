(ns orochi.proxies.proxy-common)

;; (defn append-request [component incoming new-req resp]
;;   (let [counter (swap! (get component :counter) inc)
;;         log {:count counter
;;              :incoming (dissoc incoming :throw-exceptions)
;;              :mod (dissoc new-req :throw-exceptions)
;;              :response (dissoc resp :throw-exceptions)}]
;;     (swap! (:requests component) conj log)))

(defn append-action [component action type]
  (let [counter (swap! (get component :counter) inc)
        stored-action (assoc action :type type :count counter)]
    (swap! (:actions component) conj stored-action)))
