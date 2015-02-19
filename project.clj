(defproject cerberus "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [midje "1.6.3"]
                 [ring-server "0.3.0"]
                 [ring-jetty-component "0.2.2"]
                 [ring "1.3.0-RC1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [me.raynes/conch "0.8.0"]
                 [clj-http "1.0.1"]
                 [cheshire "5.4.0"]
                 [com.stuartsierra/component "0.2.2"]
                 [com.duelinmarkers/ring-request-logging "0.2.0"]]
  :plugins [;;[lein-ring "0.8.13"]
            [lein-midje "3.1.3"]
            [quickie "0.3.6"]]
  ;;  :ring {:handler cerberus.core.handler/start-api}
  :main cerberus.core.api
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
