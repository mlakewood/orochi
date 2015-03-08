(ns orochi.core.controller-test
  (:require [orochi.core.controller :refer :all]
            [orochi.core.proxy :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [clojure.pprint :refer [pprint]]))

(deftest test-controller
  (testing "build controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port {})]
      (is (= (keys @(:proxies new-controller)) [proxy-name]))
      (is (= @(get-in @(:proxies new-controller) [proxy-name :requests]) []))
      (is (= (get-in @(:proxies new-controller) [proxy-name :options]) {:port 8090, :join? false},))
      (is (= (get-in @(:proxies new-controller) [proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in @(:proxies new-controller) [proxy-name :counter]) 0))
      (component/stop new-controller)))
  (testing "get proxy from controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port {})
          proxy (get-proxy new-controller proxy-name)]
            (is (= (keys @(:proxies new-controller)) [proxy-name]))
      (is (= @(get-in @(:proxies new-controller) [proxy-name :requests]) []))
      (is (= (get-in @(:proxies new-controller) [proxy-name :options]) {:port 8090, :join? false},))
      (is (= (get-in @(:proxies new-controller) [proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in @(:proxies new-controller) [proxy-name :counter]) 0))
      (component/stop new-controller)))
  (testing "stop proxy from controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          new-backends {:remote-addr "127.0.0.1" :server-port 5002}
          new-proxy (build-proxy (atom []) new-backends frontend-port controller {})
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port {})
          st-prox-cont (stop-proxy new-controller proxy-name)
          ]
      (is (= @(get-in @(:proxies st-prox-cont) [proxy-name :requests]) []))
      (is (= (get-in @(:proxies st-prox-cont) [proxy-name :options]) nil))
      (is (= (get-in @(:proxies st-prox-cont) [proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in @(:proxies st-prox-cont) [proxy-name :counter]) 0))
      (component/stop st-prox-cont)))
  (testing "stop all proxies from controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          frontend-port2 8091
          proxy-name 'test-proxy1'
          proxy-name2 'test-proxy2'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          new-backends {:remote-addr "127.0.0.1" :server-port 5002}
          new-proxy (build-proxy (atom []) new-backends frontend-port controller {})
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port {})
          new-controller (add-proxy started-controller proxy-name2 backends frontend-port2 {})
          st-prox-cont (stop-all-proxies new-controller)
          ]
      (is (= @(get-in @(:proxies st-prox-cont) [proxy-name :requests]) []))
      (is (= (get-in @(:proxies st-prox-cont) [proxy-name :options]) nil))
      (is (= (get-in @(:proxies st-prox-cont) [proxy-name :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in @(:proxies st-prox-cont) [proxy-name :counter]) 0))
      (is (= @(get-in @(:proxies st-prox-cont) [proxy-name2 :requests]) []))
      (is (= (get-in @(:proxies st-prox-cont) [proxy-name2 :options]) nil))
      (is (= (get-in @(:proxies st-prox-cont) [proxy-name2 :backend]) {:remote-addr "127.0.0.1", :server-port 5001}))
      (is (= @(get-in @(:proxies st-prox-cont) [proxy-name2 :counter]) 0))
      (component/stop st-prox-cont)))
  (testing "clear all proxies from controller"
    (let [controller (build-controller {})
          backend-port 5001
          frontend-port 8090
          proxy-name 'test-proxy1'
          backends {:remote-addr "127.0.0.1" :server-port backend-port}
          new-backends {:remote-addr "127.0.0.1" :server-port 5002}
          new-proxy (build-proxy (atom []) new-backends frontend-port controller {})
          started-controller (component/start controller)
          new-controller (add-proxy started-controller proxy-name backends frontend-port {})
          st-prox-cont (clear-proxies new-controller)
          ]
      (is (= @(:proxies st-prox-cont) {}))
      (component/stop st-prox-cont)))
  )



