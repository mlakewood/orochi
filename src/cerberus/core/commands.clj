(ns cerberus.core.commands
  (:import [java.lang.Runtime]
           [java.io.IOException]
           [java.lang.RuntimeException])
  (:require [com.stuartsierra.component :as component]
            [clojure.string :refer [split]]
            [clojure.java.io :refer :all]))

(defn execute
  "Executes a command-line program, returning stdout if a zero return code, else the
  error out. Takes a list of strings which represent the command & arguments"
  [& args]
  (try
    (let [process (.exec (Runtime/getRuntime) (reduce str (interleave args (iterate str " "))))]
      (if (= 0 (.waitFor  process))
          (println (reader (.getInputStream process)))
          (reader (.getErrorStream process))))
    (catch java.io.IOException ioe
      (throw (new java.lang.RuntimeException (str "Cannot run" args) ioe)))))



(defrecord command [setup command teardown]
  component/Lifecycle

  (start [component]
    (println ";; Starting command")
    (let [setup (:setup component)
          command (:command component)
          teardown (:teardown component)
          setup-res (if (not (empty? setup)) 
                      (execute setup)
                      nil)
          command-res (if (not (empty? command))
                        (execute command)
                        nil) 
          teardown-res (if (not (empty? teardown))
                        (execute command)
                        nil)]
      (assoc component :setup-res setup-res)
      (assoc component :command-res command-res)
      (assoc component :teardown-res teardown-res)))

  (stop [component]
    (println ";; Starting command")
    (let [setup (:setup component)
          command (:command component)
          teardown (:teardown component)]
      (assoc component :setup-res nil)
      (assoc component :command-res nil)
      (assoc component :teardown-res nil))))

