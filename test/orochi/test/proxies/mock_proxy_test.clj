(ns orochi.test.proxies.mock-proxy-test
  (:require [orochi.proxies.mock-proxy :refer :all]
            [orochi.test.core.dummy-api :refer [start-dummy-app]]
            [orochi.core.controller :refer [build-controller]]
            [orochi.core.proxy :refer [build-proxy]]
            [com.stuartsierra.component :as component]
            [orochi.test.core.test-utils :refer [bodify clean-request-list]]            
            [clojure.test :refer :all]
            [clojure.pprint :refer [pprint]]))

(def mock-backend
  {:type "mock-request"
   :response {:status 200
              :body "tested"}}
  )


(def reg (atom {}))

(deftest test-mock-hook
  (testing "test the mocked response"
    (try
      (let [controller (build-controller {})
            dummy-backend  (start-dummy-app 8005)
            proxy (build-proxy "test-mockh1" (atom []) mock-backend 8056 controller {})
            st-proxy (component/start proxy)
            _ (swap! reg assoc :backend dummy-backend)
            _ (swap! reg assoc :proxy st-proxy)
            res (mock-request st-proxy {})]
        (is (= {:body "tested" :status 200} res)))
      (finally
        (.stop (:backend @reg))
        (component/stop (:proxy @reg)))))
)

