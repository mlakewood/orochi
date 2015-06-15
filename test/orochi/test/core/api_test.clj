(ns orochi.test.core.api-test
  (:require [orochi.core.api :refer :all]
            [orochi.test.core.test-utils :refer [bodify scripts-path python-path]]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :refer [generate-string]]))



(def get-listing [{"name" "test-proxy-2",
                   "actions" [],
                   "backend" {"type" "proxy", "payload" {"remote-addr" "127.0.0.1", "server-port" "8002"}},
                   "options" {"join?" false, "port" 8082},
                   "command" {"started-check" (str python-path scripts-path "started-check.py http://127.0.0.1:8002/README.md"), 
                              "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                              "command" (str scripts-path "command.sh 8002"), 
                              "teardown" (str scripts-path "teardown.sh 8002"), 
                              "finished-output" nil,
                              "setup" (str scripts-path "setup.sh"), 
                              "command-result" {"out" "starting command\n",
                                                "err" ""},
                              "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                              "teardown-result" nil,
                              "finished" (str python-path scripts-path "started-check.py http://127.0.0.1:8002/README.md")}
                   },
                  {"name" "test-proxy-1",
                   "actions" [],
                   "backend" {"type" "proxy", "payload" {"remote-addr" "127.0.0.1", "server-port" "8001"}}, 
                   "options" {"join?" false, "port" 8081},
                   "command" {"started-check" (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md")
                              "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                              "command" (str scripts-path "command.sh 8001"), 
                              "teardown" (str scripts-path "teardown.sh 8001"), 
                              "finished-output" nil,
                              "setup" (str scripts-path "setup.sh"), 
                              "command-result" {"out" "starting command\n",
                                                "err" ""},
                              "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                              "teardown-result" nil,
                              "finished" (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md") }}])

(def get-instance-listing {"name" "test-proxy-1",
                   "actions" [],
                   "backend" {"type" "proxy", "payload" {"remote-addr" "127.0.0.1", "server-port" "8001"}},
                   "options" {"join?" false, "port" 8081},
                   "command" {"started-check" (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md"), 
                              "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                              "command" (str scripts-path "command.sh 8001"), 
                              "teardown" (str scripts-path "teardown.sh 8001"), 
                              "finished-output" nil,
                              "setup" (str scripts-path "setup.sh"), 
                              "command-result" {"out" "starting command\n",
                                                "err" ""},
                              "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                              "teardown-result" nil,
                              "finished" (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md" ) }})

(def after-del-listing [{"name" "test-proxy-2",
                         "actions" [],
                         "backend" {"type" "proxy" "payload" {"remote-addr" "127.0.0.1", "server-port" "8002"}},
                         "options" nil,
                         "command" {"started-check" (str python-path scripts-path "started-check.py http://127.0.0.1:8002/README.md"), 
                                    "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                                    "command" (str scripts-path "command.sh 8002"),
                                    "teardown" (str scripts-path "teardown.sh 8002"),
                                    "setup" (str scripts-path "setup.sh"),
                                    "command-result" {"out" "starting command\n",
                                                      "err" ""},
                                    "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                                    "teardown-result" {"out" "", "err" ""},
                                    "finished" (str python-path scripts-path "started-check.py http://127.0.0.1:8002/README.md"),
                                    "finished-output" {"out" 
                                                       "got exception\ngot exception\ngot exception\ngot exception\nwe got badness\n",
                                                       "err" ""}}
                         },
                        {"name" "test-proxy-1",
                         "actions" [],
                         "backend" {"type" "proxy" "payload" {"remote-addr" "127.0.0.1", "server-port" "8001"},}
                         "options" nil,
                         "command" {"started-check" (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md"),
                                    "setup-result" {"out" "Run setup\nfinished setup.sh\n", "err" ""},                    
                                    "command" (str scripts-path "command.sh 8001"),
                                    "teardown" (str scripts-path "teardown.sh 8001"),
                                    "setup" (str scripts-path "setup.sh"),
                                    "command-result" {"out" "starting command\n",
                                                      "err" ""},
                                    "started-check-result" {"out" "we got 200!\n", "err" ""},                              
                                    "teardown-result" {"out" "",
                                                        "err" ""},
                                    "finished" (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md"),
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
          command {:setup (str scripts-path "setup.sh")
                   :command (str scripts-path "command.sh 8001")
                   :started-check (str python-path  scripts-path "started-check.py http://127.0.0.1:8001/README.md")
                   :teardown (str scripts-path "teardown.sh 8001")}
          backend {:type "proxy" :payload {:remote-addr "127.0.0.1" :server-port "8099"}} 
          proxy-req {:name proxy-name :front-port front-port :backend backend :command command}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy-req)}))
          controller @(:proxies (:cont @(:controller started-comp)))
          stopped-api (component/stop started-comp)
          stopped-controller @(:proxies (:cont @(:controller started-comp)))]
      (is (= (keys controller) ["test-proxy-1"]))
      (is (= (get-in controller [proxy-name :backend]) backend))
      (is (= (get-in controller [proxy-name :options]) {:port front-port :join? false}))
      (is (= @(get-in controller [proxy-name :counter]) 0))
      (is (= @(get-in controller [proxy-name :actions]) []))
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
          ;; build the first proxy data
          proxy-name-1 "test-proxy-1"
          front-port-1 8081
          command-1 {:setup (str scripts-path "setup.sh")
                     :command (str scripts-path "command.sh 8001")
                     :started-check (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md")
                     :teardown (str scripts-path "teardown.sh 8001")}
          backend-1 {:type "proxy" :payload  {:remote-addr "127.0.0.1" :server-port "8001"}}
          proxy1-req {:name proxy-name-1 :front-port front-port-1 :backend backend-1 :command command-1}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy1-req)}))
          
          ;; build the second proxy name
          proxy-name-2 "test-proxy-2"
          front-port-2 8082
          command-2 {:setup (str scripts-path "setup.sh")
                     :command (str scripts-path "command.sh 8002")
                     :started-check (str scripts-path "../../../venv/bin/python " scripts-path "started-check.py http://127.0.0.1:8002/README.md")
                     :teardown (str scripts-path "teardown.sh 8002")}
          backend-2 {:type "proxy" :payload {:remote-addr "127.0.0.1" :server-port "8002"}}
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

          ;; build the first proxy data
          proxy-name-1 "test-proxy-1"
          front-port-1 8081
          command-1 {:setup (str scripts-path "setup.sh")
                     :command (str scripts-path "command.sh 8001")
                     :started-check (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md")
                     :teardown (str scripts-path "teardown.sh 8001")}
          backend-1 {:type "proxy" :payload {:remote-addr "127.0.0.1" :server-port "8001"}}  
          proxy1-req {:name proxy-name-1 :front-port front-port-1 :backend backend-1 :command command-1}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy1-req)}))

          ;; build the second proxy name
          proxy-name-2 "test-proxy-2"
          front-port-2 8082
          command-2 {:setup (str scripts-path "setup.sh")
                     :command (str scripts-path "command.sh 8002")
                     :started-check (str python-path scripts-path "started-check.py http://127.0.0.1:8002/README.md")
                     :teardown (str scripts-path "teardown.sh 8002")}
          backend-2 {:type "proxy" :payload {:remote-addr "127.0.0.1" :server-port "8002"}} 
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

          ;; build the first proxy data
          proxy-name-1 "test-proxy-1"
          front-port-1 8081
          command-1 {:setup (str scripts-path "setup.sh")
                     :command (str scripts-path "command.sh 8001")
                     :started-check (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md")
                     :teardown (str scripts-path "teardown.sh 8001")}
          backend-1 {:type "proxy" "payload" {:remote-addr "127.0.0.1" :server-port "8001"}} 
          proxy1-req {:name proxy-name-1 :front-port front-port-1 :backend backend-1 :command command-1}
          post-resp (bodify (client/post "http://127.0.0.1:8080/proxy" {:body (generate-string proxy1-req)}))
          
          get-resp (bodify (client/get "http://127.0.0.1:8080/proxy/test-proxy-1"))
          stopped-api (component/stop started-comp)
          mod-resp (assoc-in get-resp ["command" "command-result" "err"] "")]
      (is (= mod-resp get-instance-listing))))
  )
