(ns orochi.proxies.web-hook
  (:require [orochi.proxies.proxy-common :refer [append-action]]
            [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]))


(defn request-async [hook delay component incoming canned-response]
  (append-action component incoming :incoming)
  (append-action component hook :hook)
  (append-action component canned-response :response)
  (Thread/sleep delay)
  (let [response (client/request hook)]
    (append-action component response :response)))

(defn request-block [hook delay component incoming]
  (append-action component incoming :incoming)
  (append-action component hook :hook)
  (let [response (client/request hook)
        _ (append-action component response :response)]
    (Thread/sleep delay)
    response))

(defn web-hook [component request]
  (let [hook (get-in component [:backend :payload :request]) 
        timing (get-in component [:backend :payload :request-time :timing])
        delay (get-in component [:backend :payload :request-time :delay] 0)
        canned-response (get-in component [:backend :payload :response])
        
        response (if (= timing "during")
                   (request-block hook delay component request)
                   (future (request-async hook delay component request canned-response)))]
    (if (and (= canned-response :return-response) (= timing "during"))
      response
      (let [_ (append-action component canned-response :canned-response)]
        canned-response))))

