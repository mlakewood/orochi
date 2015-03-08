(ns orochi.core.commands-test
  (:require [orochi.core.commands :refer :all]
            [clojure.test :refer :all]
            [clj-commons-exec :as exec]
            [clojure.pprint :refer [pprint]]
            [orochi.core.test-utils :refer :all]
            [com.stuartsierra.component :as component]))

(deftest test-command
  (testing "run command success"
    (let [base-path "/Users/underplank/projects/orochi/scripts/"
          setup-com (str base-path "setup.sh")
          com (str base-path "command.sh")
          started-com (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8000/README.md")
          teardown-com (str base-path "teardown.sh")
          command (build-command setup-com com started-com teardown-com)
          start-com (component/start command)
          stop-com (component/stop start-com)]
      (is (= (:setup start-com) setup-com))
      (is (= (:command start-com) com))
      (is (= (:started-check start-com) started-com))
      (is (= (:ready? start-com) true))
      (is (= (:teardown start-com) teardown-com))
      (is (= (.toString (get-in start-com [:setup-res :out])) "Run setup\nfinished setup.sh\n"))
      (is (= (.toString (get-in start-com [:setup-res :err])) ""))
      (is (= (.toString (get-in start-com [:command-res :out])) "starting command\nstopping\n"))
      (is (substring? (.toString (get-in start-com [:started-res :out])) "we got 200!"))
      (is (= (.toString (get-in start-com [:started-res :err])) ""))
      (is (= (.toString (get-in stop-com [:teardown-res :out])) ""))
      (is (= (.toString (get-in stop-com [:teardown-res :err])) ""))))
  (testing "run command success didnt start"
    (let [base-path "/Users/underplank/projects/orochi/scripts/"
          setup-com (str base-path "setup.sh")
          com (str base-path "command.sh")
          started-com (str base-path "../venv/bin/python " base-path "started-check.py http://127.0.0.1:8001/README.md")
          teardown-com (str base-path "teardown.sh")
          command (build-command setup-com com started-com teardown-com)
          start-com (component/start command)
          stop-com (component/stop start-com)
          ]
      (is (= (:setup start-com) setup-com))
      (is (= (:command start-com) com))
      (is (= (:started-check start-com) started-com))
      (is (= (:ready? start-com) false))
      (is (= (:teardown start-com) teardown-com))
      (is (= (.toString (get-in start-com [:setup-res :out])) "Run setup\nfinished setup.sh\n"))
      (is (= (.toString (get-in start-com [:setup-res :err])) ""))
      (is (= (.toString (get-in start-com [:command-res :out])) "starting command\nstopping\n"))
      (is (= (.toString (get-in start-com [:started-res :out])) "got exception\ngot exception\ngot exception\ngot exception\nwe got badness\n"))
      (is (= (.toString (get-in start-com [:started-res :err])) ""))
      (is (= (.toString (get-in stop-com [:teardown-res :out])) ""))
  (is (= (.toString (get-in stop-com [:teardown-res :err])) ""))
  )))


