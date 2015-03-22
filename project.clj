(defproject orochi "0.1.0-SNAPSHOT"
  :description "An integration test framework for http services"
  :url "https://github.com/mlakewood/orochi"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [midje "1.6.3"]
                 [org.clojars.hozumi/clj-commons-exec "1.2.0"]
                 [ring-server "0.4.0"]
                 [ring-jetty-component "0.2.2"]
                 [ring "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [me.raynes/conch "0.8.0"]
                 [clj-http "1.0.1"]
                 [cheshire "5.4.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [com.duelinmarkers/ring-request-logging "0.2.0"]]
  :plugins [;;[lein-ring "0.8.13"]
            [lein-midje "3.1.3"]
            [quickie "0.3.6"]
            [com.jakemccrary/lein-test-refresh "0.6.0"]]
  ;;  :ring {:handleorochi.handler/start-api}
  :main orochi.core.api
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:auto-clean false
             :aot :all}})
