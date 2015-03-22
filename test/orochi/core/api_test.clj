(ns orochi.core.api-test
  (:require [orochi.core.api :refer :all]
            [orochi.core.test-utils :refer [bodify]]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :refer [generate-string]]))





(def get-listing [{"name" "test-proxy-2",
                   "requests" [],
                   "backend" {"remote-addr" "127.0.0.1", "server-port" "8002"},
                   "options" {"join?" false, "port" 8082},
                   "command" {"started-check" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8002/README.md",
                              "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                              "command" "/Users/underplank/projects/orochi/scripts/command.sh 8002",
                              "teardown" "/Users/underplank/projects/orochi/scripts/teardown.sh",
                              "finished-output" nil,
                              "setup" "/Users/underplank/projects/orochi/scripts/setup.sh",
                              "command-result" {"out" "starting command\n",
                                                "err" ""},
                              "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                              "teardown-result" nil,
                              "finished" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8002/README.md"}
                   },
                  {"name" "test-proxy-1",
                   "requests" [],
                   "backend" {"remote-addr" "127.0.0.1", "server-port" "8001"},
                   "options" {"join?" false, "port" 8081},
                   "command" {"started-check" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8001/README.md",
                              "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                              "command" "/Users/underplank/projects/orochi/scripts/command.sh 8001",
                              "teardown" "/Users/underplank/projects/orochi/scripts/teardown.sh",
                              "finished-output" nil,
                              "setup" "/Users/underplank/projects/orochi/scripts/setup.sh",
                              "command-result" {"out" "starting command\n",
                                                "err" ""},
                              "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                              "teardown-result" nil,
                              "finished" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8001/README.md"}}])

(def get-instance-listing {"name" "test-proxy-1",
                   "requests" [],
                   "backend" {"remote-addr" "127.0.0.1", "server-port" "8001"},
                   "options" {"join?" false, "port" 8081},
                   "command" {"started-check" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8001/README.md",
                              "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                              "command" "/Users/underplank/projects/orochi/scripts/command.sh 8001",
                              "teardown" "/Users/underplank/projects/orochi/scripts/teardown.sh",
                              "finished-output" nil,
                              "setup" "/Users/underplank/projects/orochi/scripts/setup.sh",
                              "command-result" {"out" "starting command\n",
                                                "err" ""},
                              "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                              "teardown-result" nil,
                              "finished" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8001/README.md"}})



(def after-del-listing [{"name" "test-proxy-2",
                         "requests" [],
                         "backend" {"remote-addr" "127.0.0.1", "server-port" "8002"},
                         "options" nil,
                         "command" {"started-check" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8002/README.md",
                                    "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                                    "command" "/Users/underplank/projects/orochi/scripts/command.sh 8002",
                                    "teardown" "/Users/underplank/projects/orochi/scripts/teardown.sh",
                                    "setup" "/Users/underplank/projects/orochi/scripts/setup.sh",
                                    "command-result" {"out" "starting command\nstopping\n",
                                                      "err" ""},
                                    "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                                    "teardown-result" {"out" "", "err" ""},
                                    "finished" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8002/README.md",
                                    "finished-output" {"out" 
                                                       "got exception\ngot exception\ngot exception\ngot exception\nwe got badness\n",
                                                       "err" ""}}
                         },
                        {"name" "test-proxy-1",
                         "requests" [],
                         "backend" {"remote-addr" "127.0.0.1", "server-port" "8001"},
                         "options" nil,
                         "command" {"started-check" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8001/README.md",
                                    "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                                    "command" "/Users/underplank/projects/orochi/scripts/command.sh 8001",
                                    "teardown" "/Users/underplank/projects/orochi/scripts/teardown.sh",
                                    "setup" "/Users/underplank/projects/orochi/scripts/setup.sh",
                                    "command-result" {"out" "starting command\nstopping\n",
                                                      "err" ""},
                                    "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                                    "teardown-result" {"out" "",
                                                        "err" "No matching processes belonging to you were found\n"},
                                    "finished" "/Users/underplank/projects/orochi/scripts/../venv/bin/python /Users/underplank/projects/orochi/scripts/started-check.py http://127.0.0.1:8001/README.md",
                                    "finished-output" {"out"
                                                       "got exception\ngot exception\ngot exception\ngot exception\nwe got badness\n",
                                                       "err" ""}}}])

(def expected-not-found {:orig-content-encoding nil,
                    :trace-redirects ["http://127.0.0.1:8080/proxy/123"],
                    :status 404,
                    :body "{\"error\":\"Proxy 123 does not exist\"}"})


(deftest test-api
  (testing "get of proxy"
    (let [api (build-api {:port 8080 :join? false})
          started-comp (component/start api)
          get-req (bodify (client/get "http://127.0.0.1:8080/proxy"))
          not-found-resp (client/get "http://127.0.0.1:8080/proxy/123" {:throw-exceptions false})
          delete-req-inst (bodify (client/delete "http://127.0.0.1:8080/proxy/123"))
          expected {"status" "tested"}
          expected-inst {"status" "tested 123"}
          stopped-api (component/stop started-comp)]
      (is (= get-req []))
      (is (= (dissoc not-found-resp :headers :request-time) expected-not-found))
      (is (= delete-req-inst expected-inst))))
  (testing "post to proxy"
    (let [api (build-api {:port 8080 :join? false})
          started-comp (component/start api)
          proxy-name "test-proxy-1"
          front-port 8081
          base-path "/Users/underplank/projects/orochi/scripts/"
          command {:setup (str base-path "setup.sh")
                   :command (str base-path "command.sh")
                   :started-check (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8000/README.md")
                   :teardown (str base-path "teardown.sh")}
          backend {:remote-addr "127.0.0.1" :server-port "8099"} 
          proxy-req {:name proxy-name :front-port front-port :backend backend :command command}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy-req)}))
          controller @(:proxies (:cont @(:controller started-comp)))
          stopped-api (component/stop started-comp)
          stopped-controller @(:proxies (:cont @(:controller started-comp)))]
      (is (= (keys controller) ["test-proxy-1"]))
      (is (= (get-in controller [proxy-name :backend]) backend))
      (is (= (get-in controller [proxy-name :options]) {:port front-port :join? false}))
      (is (= @(get-in controller [proxy-name :counter]) 0))
      (is (= @(get-in controller [proxy-name :requests]) []))
      (is (= (:ready? (get-in controller [proxy-name :command])) true))
      (is (not (= (:setup-res (get-in controller [proxy-name :command])) nil)))
      (is (not (= (:command-res (get-in controller [proxy-name :command])) nil)))
      (is (not (= (:started-res (get-in controller [proxy-name :command])) nil)))
      (is (= (:teardown-res (get-in controller [proxy-name :command])) nil))
      (is (not (= (:teardown-res (get-in stopped-controller [proxy-name :command])) nil)))
      (is (= (:ready? (get-in stopped-controller [proxy-name :command])) false))
      ))
  (testing "get proxy list"
    (let [api (build-api {:port 8080 :join? false})
          started-comp (component/start api)
          base-path "/Users/underplank/projects/orochi/scripts/"

          ;; build the first proxy data
          proxy-name-1 "test-proxy-1"
          front-port-1 8081
          command-1 {:setup (str base-path "setup.sh")
                     :command (str base-path "command.sh 8001")
                     :started-check (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8001/README.md")
                     :teardown (str base-path "teardown.sh")}
          backend-1 {:remote-addr "127.0.0.1" :server-port "8001"} 
          proxy1-req {:name proxy-name-1 :front-port front-port-1 :backend backend-1 :command command-1}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy1-req)}))
          
          ;; build the second proxy name
          proxy-name-2 "test-proxy-2"
          front-port-2 8082
          command-2 {:setup (str base-path "setup.sh")
                     :command (str base-path "command.sh 8002")
                     :started-check (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8002/README.md")
                     :teardown (str base-path "teardown.sh")}
          backend-2 {:remote-addr "127.0.0.1" :server-port "8002"} 
          proxy2-req {:name proxy-name-2 :front-port front-port-2 :backend backend-2 :command command-2}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy2-req)}))

          get-resp (bodify (client/get "http://127.0.0.1:8080/proxy"))
          stopped-api (component/stop started-comp)
          ;; mod for the sake of timestamps
          mod-resp [(assoc-in (first get-resp) ["command" "command-result" "err"] "")
                    (assoc-in (last get-resp) ["command" "command-result" "err"] "")
                    ]]
      (is (= mod-resp get-listing))
      ))
  (testing "reset all proxies"
    (let [api (build-api {:port 8080 :join? false})
          started-comp (component/start api)
          base-path "/Users/underplank/projects/orochi/scripts/"

          ;; build the first proxy data
          proxy-name-1 "test-proxy-1"
          front-port-1 8081
          command-1 {:setup (str base-path "setup.sh")
                     :command (str base-path "command.sh 8001")
                     :started-check (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8001/README.md")
                     :teardown (str base-path "teardown.sh")}
          backend-1 {:remote-addr "127.0.0.1" :server-port "8001"} 
          proxy1-req {:name proxy-name-1 :front-port front-port-1 :backend backend-1 :command command-1}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy1-req)}))
          
          ;; build the second proxy name
          proxy-name-2 "test-proxy-2"
          front-port-2 8082
          command-2 {:setup (str base-path "setup.sh")
                     :command (str base-path "command.sh 8002")
                     :started-check (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8002/README.md")
                     :teardown (str base-path "teardown.sh")}
          backend-2 {:remote-addr "127.0.0.1" :server-port "8002"} 
          proxy2-req {:name proxy-name-2 :front-port front-port-2 :backend backend-2 :command command-2}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy2-req)}))
          post-resp (bodify (client/delete "http://127.0.0.1:8080/proxy"))

          get-resp (bodify (client/get "http://127.0.0.1:8080/proxy"))
          stopped-api (component/stop started-comp)
          mod-resp [(assoc-in (first get-resp) ["command" "command-result" "err"] "")
                    (assoc-in (last get-resp) ["command" "command-result" "err"] "")
                    ]]
      (is (= mod-resp after-del-listing))
      ))
  (testing "get-individual proxies"
    (let [api (build-api {:port 8080 :join? false})
          started-comp (component/start api)
          base-path "/Users/underplank/projects/orochi/scripts/"

          ;; build the first proxy data
          proxy-name-1 "test-proxy-1"
          front-port-1 8081
          command-1 {:setup (str base-path "setup.sh")
                     :command (str base-path "command.sh 8001")
                     :started-check (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8001/README.md")
                     :teardown (str base-path "teardown.sh")}
          backend-1 {:remote-addr "127.0.0.1" :server-port "8001"} 
          proxy1-req {:name proxy-name-1 :front-port front-port-1 :backend backend-1 :command command-1}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy1-req)}))
          
          get-resp (bodify (client/get "http://127.0.0.1:8080/proxy/test-proxy-1"))
          stopped-api (component/stop started-comp)
          mod-resp (assoc-in get-resp ["command" "command-result" "err"] "")]
      (is (= mod-resp get-instance-listing)))))
