(ns orochi.test.core.proxy-test
  (:require [orochi.core.proxy :refer :all]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [cheshire.core :refer :all]
            [clj-http.client :as client]
            [orochi.test.core.dummy-api :refer [start-dummy-app]]
            [orochi.test.core.test-utils :refer [bodify clean-request-list]]
            [orochi.core.controller :refer [build-controller]]
            [clojure.pprint :refer [pprint]]))

(def test [{:ssl-client-cert nil,
  :remote-addr "127.0.0.1",
  :params {:* "foo/bar"},
  :type :incoming,
  :route-params {:* "foo/bar"},
  :server-port 8089,
  :content-length nil,
  :count 1,
  :content-type nil,
  :headers {"accept-encoding" "gzip, deflate",
               "connection" "close",
               "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
               "host" "127.0.0.1:8089"},
  :character-encoding nil,
  :uri "/foo/bar",
  :server-name "127.0.0.1",
  :query-string nil,
  :scheme :http,
  :request-method :get}
 {:ssl-client-cert nil,
  :remote-addr "127.0.0.1",
  :params {:* "foo/bar"},
  :type :mod,
  :route-params {:* "foo/bar"},
  :server-port 5001,
  :content-length nil,
  :count 2,
  :content-type nil,
  :headers {"accept-encoding" "gzip, deflate",
               "connection" "close",
               "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
               "host" "127.0.0.1:8089"},
  :throw-exceptions false,
  :character-encoding nil,
  :uri "/foo/bar",
  :server-name "127.0.0.1",
  :query-string nil,
  :scheme :http,
  :request-method :get}
 {:count 3,
  :type :response,
  :orig-content-encoding nil,
  :trace-redirects [],
  :status 200,
:headers
              {"Server" "Jetty(7.6.13.v20130916)",
               "Connection" "close",
               "Content-Type" "application/json; charset=utf-8"}
  :body "{\"status\":\"tested\"}"
  }
 {:ssl-client-cert nil,
  :remote-addr "127.0.0.1",
  :params {:* "foo/bar"},
  :type :incoming,
  :route-params {:* "foo/bar"},
  :server-port 8089,
  :content-length nil,
  :count 4,
  :headers {"accept-encoding" "gzip, deflate",
               "connection" "close",
               "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
               "host" "127.0.0.1:8089"},
  :content-type nil,
  :character-encoding nil,
  :uri "/foo/bar",
  :server-name "127.0.0.1",
  :query-string nil,
  :scheme :http,
  :request-method :get}
 {:ssl-client-cert nil,
  :remote-addr "127.0.0.1",
  :params {:* "foo/bar"},
  :type :mod,
  :route-params {:* "foo/bar"},
  :server-port 5001,
  :content-length nil,
  :count 5,
  :content-type nil,
  :throw-exceptions false,
  :character-encoding nil,
  :uri "/foo/bar",
  :server-name "127.0.0.1",
  :query-string nil,
  :scheme :http,
  :headers {"accept-encoding" "gzip, deflate",
               "connection" "close",
               "user-agent" "Apache-HttpClient/4.3.5 (java 1.5)",
               "host" "127.0.0.1:8089"},
  :request-method :get}
 {:count 6,
  :type :response,
  :orig-content-encoding nil,
  :trace-redirects [],
  :status 200,
  :headers
              {"Server" "Jetty(7.6.13.v20130916)",
               "Connection" "close",
               "Content-Type" "application/json; charset=utf-8"},
   :body "{\"status\":\"tested\"}"
  }])



(def reg (atom {}))


(deftest test-proxy
  (testing "build the proxy"
    (try
      (let [backend-port 5001
            frontend-port 8089
            dummy-backend  (start-dummy-app backend-port)
            _ (swap! reg assoc :backend dummy-backend)
            controller (build-controller {})
            backend {:type "proxy" :payload {:remote-addr "127.0.0.1" :server-port backend-port}}
            proxy (build-proxy "test-proxy-1" (atom []) backend frontend-port controller {})
            started-proxy (component/start proxy)
            _ (swap! reg assoc :proxy started-proxy)
            res1 (bodify (client/get (str "http://127.0.0.1:" frontend-port "/foo/bar")))
            res2 (bodify (client/get (str "http://127.0.0.1:" frontend-port "/foo/bar")))
            request-list @(:actions started-proxy)
            req (map clean-request-list request-list)]
        (is (= res1 {"status" "tested"}))
        (is (= test req)))
      (finally
        (.stop (:backend @reg))
        (component/stop (:proxy @reg))
        (swap! reg {}))))
  )




