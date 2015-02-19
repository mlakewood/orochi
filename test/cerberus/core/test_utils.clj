(ns cerberus.core.test-utils
  (:require [cheshire.core :refer :all]))

(defn bodify [response]
  (parse-string (:body response)))

(defn clean-request-list [result]
  (let [incoming (update-in result [:incoming] #(dissoc %1 :body))
        mod-r (update-in incoming [:mod] #(dissoc %1 :body))
        response (update-in mod-r [:response :headers] #(dissoc %1 "Date"))
        response (update-in response [:response] #(dissoc %1 :request-time))
        ]
    response))
