(ns orochi.test.core.test-utils
  (:require [cheshire.core :refer :all]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]))

(def scripts-path (.getPath (io/resource "test/scripts/")))
(def python-path (str scripts-path "../../../venv/bin/python "))

(defn bodify [response]
  (parse-string (:body response)))

(defn clean-request-list [result]
  (let [data result
        data (if (instance? org.eclipse.jetty.server.HttpInput (:body data)) 
               (dissoc data :body)
               data)
        data (dissoc data :request-time)
        data (if (contains? data :headers)
               (update-in data [:headers] #(dissoc %1 "Date"))
               data)]
    data))

(defn substring? [full-str sub]
  (if (not= (.indexOf full-str sub) -1)
    true
    false))
