(ns cerberus.core.proxy-test
  (:require [cerberus.core.proxy :refer :all]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [cheshire.core :refer :all]
            [clj-http.client :as client]
            [cerberus.core.dummy-api :refer [start-dummy-app]]
            [cerberus.core.test-utils :refer [bodify clean-request-list]]
            [cerberus.core.controller :refer [build-controller]]
            [clojure.pprint :refer [pprint]]))


(def result [{:count 1,
    :incoming
    {:ssl-client-cert nil,
     :remote-addr "127.0.0.1",
     :params {:* "foo/bar"},
     :route-params {:* "foo/bar"},
     :headers
     {"host" "127.0.0.1:8089",
      "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
      "accept-encoding" "gzip, deflate",
      "connection" "close"},
     :server-port 8089,
     :content-length nil,
     :content-type nil,
     :character-encoding nil,
     :uri "/foo/bar",
     :server-name "127.0.0.1",
     :query-string nil,
     :scheme :http,
     :request-method :get},
    :mod
    {:ssl-client-cert nil,
     :remote-addr "127.0.0.1",
     :params {:* "foo/bar"},
     :route-params {:* "foo/bar"},
     :headers
     {"host" "127.0.0.1:8089",
      "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
      "accept-encoding" "gzip, deflate",
      "connection" "close"},
     :server-port 5001,
     :content-length nil,
     :content-type nil,
     :character-encoding nil,
     :uri "/foo/bar",
     :server-name "127.0.0.1",
     :query-string nil,
     :scheme :http,
     :request-method :get},
    :response
    {:orig-content-encoding nil,
     :trace-redirects [],
     :status 200,
     :headers
     {"Server" "Jetty(7.6.13.v20130916)",
      "Connection" "close",
      "Content-Type" "application/json; charset=utf-8",},
     :body "{\"status\":\"tested\"}"}}
   {:count 2,
    :incoming
    {:ssl-client-cert nil,
     :remote-addr "127.0.0.1",
     :params {:* "foo/bar"},
     :route-params {:* "foo/bar"},
     :headers
     {"host" "127.0.0.1:8089",
      "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
      "accept-encoding" "gzip, deflate",
      "connection" "close"},
     :server-port 8089,
     :content-length nil,
     :content-type nil,
     :character-encoding nil,
     :uri "/foo/bar",
     :server-name "127.0.0.1",
     :query-string nil,
     :scheme :http,
     :request-method :get},
    :mod
    {:ssl-client-cert nil,
     :remote-addr "127.0.0.1",
     :params {:* "foo/bar"},
     :route-params {:* "foo/bar"},
     :headers
     {"host" "127.0.0.1:8089",
      "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
      "accept-encoding" "gzip, deflate",
      "connection" "close"},
     :server-port 5001,
     :content-length nil,
     :content-type nil,
     :character-encoding nil,
     :uri "/foo/bar",
     :server-name "127.0.0.1",
     :query-string nil,
     :scheme :http,
     :request-method :get},
    :response
    {:orig-content-encoding nil,
     :trace-redirects [],
     :status 200,
     :headers
     {"Server" "Jetty(7.6.13.v20130916)",
      "Connection" "close",
      "Content-Type" "application/json; charset=utf-8",},
     :body "{\"status\":\"tested\"}"}}])


(deftest test-proxy
  (testing "build the proxy"
    (let [backend-port 5001
          frontend-port 8089
          dummy-backend  (start-dummy-app backend-port)
          controller (build-controller {})
          proxy (build-proxy (atom []) {:remote-addr "127.0.0.1" :server-port backend-port} frontend-port controller)
          started-proxy (component/start proxy)
          res1 (bodify (client/get (str "http://127.0.0.1:" frontend-port "/foo/bar")))
          res2 (bodify (client/get (str "http://127.0.0.1:" frontend-port "/foo/bar")))
          request-list (:requests started-proxy)
          req (map clean-request-list @request-list)
          ]
      (is (= res1 {"status" "tested"}))
      (is (= result req))
      (.stop dummy-backend)
      (component/stop started-proxy)
      )))


