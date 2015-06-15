(ns orochi.test.proxies.web-hook-test
  (:require [orochi.proxies.web-hook :refer :all]
            [orochi.test.core.dummy-api :refer [start-dummy-app]]
            [orochi.core.controller :refer [build-controller]]
            [orochi.core.proxy :refer [build-proxy]]
            [com.stuartsierra.component :as component]
            [orochi.test.core.test-utils :refer [bodify clean-request-list]]            
            [clojure.test :refer :all]
            [clojure.pprint :refer [pprint]]))

(def during-backend
  {:type "web-hook"
   :payload {:request-time {:timing :during :delay 200}
             :request {:method :get
                       :url "http://127.0.0.1:8005/foo/bar"}
             :response {:status 200
                        :body "tested"}}}
  )

(def after-backend
  {:type "web-hook"
   :payload {:request-time {:timing :after :delay 2000}
             :request {:method :get
                       :url "http://127.0.0.1:8005/foo/after"}
             :response {:status 200
                        :body "canned"}}}
  )


(def recorded-actions [{:count 1, :type :incoming}
           {:method :get,
            :type :hook,
            :count 2,
            :url "http://127.0.0.1:8005/foo/bar"}
           {:count 3,
            :type :response,
            :orig-content-encoding nil,
            :trace-redirects ["http://127.0.0.1:8005/foo/bar"],
            :status 200,
            :headers
            {"Server" "Jetty(7.6.13.v20130916)",
             "Connection" "close",
             "Content-Type" "application/json; charset=utf-8"},
            :body "{\"status\":\"tested\"}"}])

(def async-before-hook [
                        {:count 1, :type :incoming}
                        {:method :get,
                         :type :hook,
                         :count 2,
                         :url "http://127.0.0.1:8005/foo/after"}
                        {:type :response, 
                         :status 200, 
                         :count 3, 
                         :body "canned"}])

(def async-after-hook [
                        {:count 1, :type :incoming}
                        {:method :get,
                         :type :hook,
                         :count 2,
                         :url "http://127.0.0.1:8005/foo/after"}
                        {:type :response, 
                         :status 200, 
                         :count 3, 
                         :body "canned"}
                        {:count 4,
                         :type :response,
                         :orig-content-encoding nil,
                         :trace-redirects ["http://127.0.0.1:8005/foo/after"],
                         :status 200,
                         :headers
                         {"Server" "Jetty(7.6.13.v20130916)",
                          "Connection" "close",
                          "Content-Type" "application/json; charset=utf-8"},
                         :body "{\"status\":\"tested\"}"}])

(def reg (atom {}))

(deftest test-web-hook
  (testing "test the before with delay"
    (try
      (let [controller (build-controller {})
            dummy-backend  (start-dummy-app 8005)
            proxy (build-proxy "test-webh1" (atom []) during-backend 8056 controller {})
            st-proxy (component/start proxy)
            _ (swap! reg assoc :backend dummy-backend)
            _ (swap! reg assoc :proxy st-proxy)
            res (web-hook st-proxy {})
            actions (map clean-request-list @(:actions st-proxy))]
        (is (= {:body "tested" :status 200} res))
        (is (= recorded-actions actions))) ;; <- This is wrong. we should have entries in here.
      (finally
        (.stop (:backend @reg))
        (component/stop (:proxy @reg)))))
  (testing "test the after with delay"
    (try
      (let [controller (build-controller {})
            dummy-backend  (start-dummy-app 8005)
            proxy (build-proxy "test-webh1" (atom []) after-backend 8056 controller {})
            st-proxy (component/start proxy)
            _ (swap! reg assoc :backend dummy-backend)
            _ (swap! reg assoc :proxy st-proxy)
            res (web-hook st-proxy {})]
        (is (= {:status 200, :body "canned"} res))
        (is (= async-before-hook (map clean-request-list @(:actions st-proxy))))
        (Thread/sleep 5000)
        (is (= async-after-hook (map clean-request-list @(:actions st-proxy))))
        (is (= {:body "canned" :status 200} res)))
      (finally
        (.stop (:backend @reg))
        (component/stop (:proxy @reg)))))
)

