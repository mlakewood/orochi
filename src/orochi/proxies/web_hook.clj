(ns orochi.proxies.web-hook
  (:require [orochi.proxies.proxy-common :refer [append-request]]
            [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]))


(defn request-async [hook delay]
  (println "in the async")
  (Thread/sleep delay)
  (client/request hook))

(defn request-block [hook delay]
  (println "in the block")
  (let [response (client/request hook)]
    (Thread/sleep delay)
    response))


(defn web-hook [component request]
  (let [hook (get-in component [:backend :payload :request])
        timing (get-in component [:backend :payload :request-time :timing])
        delay (get-in component [:backend :payload :request-time :delay] 0)
        canned-response (get-in component [:backend :payload :response])
        response (if (= timing  :during)
                   (request-block hook delay)
                   (future (request-async hook delay)))
        ]
    (if (and (= canned-response :return-response) (= timing :during))
      response
      canned-response)))

