(ns orochi.proxies.pass-through-proxy
  (:require [orochi.proxies.proxy-common :refer [append-action]]
            [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]))

(defn proxy-request [component request]
  (append-action component (dissoc request :throw-exceptions) :incoming)
  (let [new-req (merge request (:payload (:backend component)) {:throw-exceptions false})
        _ (append-action component new-req :mod)
        resp (client/request new-req)]
    (append-action component resp :response)
    resp))

