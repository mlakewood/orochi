(ns cerberus.core.controller-test
  (:require [cerberus.core.controller :refer :all]
            [cerberus.core.proxy :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]))


(deftest test-controller
  (testing "build controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port)
      
          ]
      (is (= (keys (get-in new-controller [:proxies])) [proxy-name]))
      (is (= @(get-in new-controller [:proxies proxy-name :requests]) []))
      (is (= (get-in new-controller [:proxies proxy-name :options]) {:port 8090, :join? false},))
      (is (= (get-in new-controller [:proxies proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in new-controller [:proxies proxy-name :counter]) 0))
      (component/stop started-controller))))



(deftest test-controller
  (testing "get proxy from controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port)
          proxy (get-proxy new-controller proxy-name)
          ]
      (is (= @(get-in new-controller [:proxies proxy-name :requests]) []))
      (is (= (get-in new-controller [:proxies proxy-name :options]) {:port 8090, :join? false},))
      (is (= (get-in new-controller [:proxies proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in new-controller [:proxies proxy-name :counter]) 0))
      (component/stop started-controller))))


(deftest test-controller
  (testing "update proxy from controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          new-backends {:remote-addr "127.0.0.1" :server-port 5002}
          new-proxy (build-proxy (atom []) new-backends frontend-port controller)
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port)
          updated-controller (modify-proxy new-controller proxy-name new-proxy)
          ]
      (is (= @(get-in @(:proxies updated-controller) [proxy-name :requests]) []))
      (is (= (get-in @(:proxies updated-controller) [proxy-name :options]) {:port 8090, :join? false},))
      (is (= (get-in @(:proxies updated-controller) [proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5002}))
      (is (= @(get-in @(:proxies updated-controller) [proxy-name :counter]) 0))
      (component/stop started-controller))))



