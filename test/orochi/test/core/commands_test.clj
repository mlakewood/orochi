(ns orochi.test.core.commands-test
  (:require [orochi.core.commands :refer :all]
            [clojure.test :refer :all]
            [clj-commons-exec :as exec]
            [clojure.pprint :refer [pprint]]
            [orochi.test.core.test-utils :refer [bodify scripts-path python-path substring?]]
            [com.stuartsierra.component :as component]))

(def setup-output "Run setup\nfinished setup.sh\n")


(deftest test-command
  (testing "run command success"
    (let [setup-com (str scripts-path "setup.sh")
          com (str scripts-path "command.sh 8001")
          started-com (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md")
          teardown-com (str scripts-path "teardown.sh 8001")
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
      (is (= (.toString (get-in start-com [:command-res :out])) "starting command\n"))
      (is (substring? (.toString (get-in start-com [:started-res :out])) "we got 200!"))
      (is (= (.toString (get-in start-com [:started-res :err])) ""))
      (is (= (.toString (get-in stop-com [:teardown-res :out])) ""))
      (is (= (.toString (get-in stop-com [:teardown-res :err])) ""))))
  (testing "run command success didnt start"
    (let [setup-com (str scripts-path "setup.sh")
          com (str scripts-path "command.sh 8000")
          ;; The started-check command is using the wrong port on purpose
          started-com (str python-path scripts-path "started-check.py http://127.0.0.1:8001/README.md")
          teardown-com (str scripts-path "teardown.sh 8000")
          command (build-command setup-com com started-com teardown-com)
          start-com (component/start command)
          stop-com (component/stop start-com)
          ]
      (is (= (:setup start-com) setup-com))
      (is (= (:command start-com) com))
      (is (= (:started-check start-com) started-com))
      (is (= (:ready? start-com) false))
      (is (= (:teardown start-com) teardown-com))
      (is (= (.toString (get-in start-com [:setup-res :out])) setup-output))
      (is (= (.toString (get-in start-com [:setup-res :err])) ""))
      (is (= (.toString (get-in start-com [:command-res :out])) "starting command\n"))
      (is (= (.toString (get-in start-com [:started-res :out])) "got exception\ngot exception\ngot exception\ngot exception\nwe got badness\n"))
      (is (= (.toString (get-in start-com [:started-res :err])) ""))
      (is (= (.toString (get-in stop-com [:teardown-res :out])) ""))
      (is (not (= (.toString (get-in stop-com [:teardown-res :err])) "")))
      ))
  )

