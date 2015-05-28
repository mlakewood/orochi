(ns orochi.test.proxies.web-hook-test
  (:require [orochi.proxies.web-hook :refer :all]
            [orochi.test.core.dummy-api :refer [start-dummy-app record-request]]
            [clojure.test :refer :all]
            [clojure.pprint :refer [pprint]]))

(def delay-backend
  {:type "web-hook"
   :payload {:request-time {:timing :during :delay 200}
             :request {:method :get
                       :url "http://127.0.0.1:8005/foo/bar"}
             :response {:status 200
                        :body "tested"}}}
  )

(def after-backend
  {:type "web-hook"
   :payload {:request-time {:timing :after :delay 200}
             :request {:method :get
                       :url "http://127.0.0.1:8005/foo/after"}
             :response {:status 200
                        :body "canned"}}}
  )

(def reg (atom {}))

(deftest test-web-hook
  ;; (testing "test the before with delay"
  ;;   (try
  ;;     (let [comp {:backend delay-backend}
  ;;           dummy-backend  (start-dummy-app 8005)
  ;;           _ (swap! reg assoc :backend dummy-backend)
  ;;           res (web-hook comp {})]
  ;;       (pprint @record-request)
  ;;       (is (= res {:body "tested" :status 200})))
  ;;     (finally
  ;;       (.stop (:backend @reg))
  ;;       (reset! record-request []))))
  (testing "test the after with delay"
    (try
      (let [comp {:backend after-backend}
            dummy-backend  (start-dummy-app 8005)
            _ (swap! reg assoc :backend dummy-backend)
            res (web-hook comp {})]
        (is (= @record-request []))
        (Thread/sleep 300)
        (is (= @record-request []))
        (is (= res {:body "canned" :status 200})))
      (finally
        (.stop (:backend @reg))
        (reset! record-request [])))))
