(ns orochi.proxies.pass-through-proxy
  (:require [orochi.proxies.proxy-common :refer [append-request]]
            [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]))

(defn proxy-request [component request]
  (let [new-req (merge request (:payload (:backend component)) {:throw-exceptions false})
        resp (client/request new-req)]
    (append-request component request new-req resp)
    resp))

