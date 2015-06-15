(ns orochi.proxies.mock-proxy
  (:require [orochi.proxies.proxy-common :refer [append-action]]
            [clojure.pprint :refer [pprint]]))

(defn mock-request [component request]
  (pprint component)
  (let [response (get-in component [:backend :response])]
    (append-action component request :incoming)
    (append-action component request :response)
    response))

