(ns cerberus.core.api-test
  (:require [cerberus.core.api :refer :all]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [cheshire.core :refer :all]
            [clj-http.client :as client]))

(defn bodify [response]
  (parse-string (:body response)))

(deftest test-api
  (testing "get of proxy"
    (let [jet (jetty/run-jetty (get-app (build-api 8080)) {:port 8080 :join? false})
          get-req (bodify (client/get "http://127.0.0.1:8080/proxy"))
          post-req (bodify (client/post "http://127.0.0.1:8080/proxy"))
          del-req (bodify (client/delete "http://127.0.0.1:8080/proxy"))
          get-req-inst (bodify (client/get "http://127.0.0.1:8080/proxy/123"))
          put-req-inst (bodify (client/put "http://127.0.0.1:8080/proxy/123"))
          delete-req-inst (bodify (client/delete "http://127.0.0.1:8080/proxy/123"))
          expected {"status" "tested"}
          expected-inst {"status" "tested 123"}]
      (is (= get-req expected))
      (is (= post-req expected))
      (is (= del-req expected))
      (is (= get-req-inst expected-inst))
      (is (= put-req-inst expected-inst))
      (is (= delete-req-inst expected-inst))
      (.stop jet))))


