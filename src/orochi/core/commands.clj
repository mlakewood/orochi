(ns orochi.core.commands
  (:import [java.io.ByteArrayOutputStream])
  (:require [com.stuartsierra.component :as component]
            [clojure.string :refer [split]]
            [orochi.core.serializer :as serializer]
            [clojure.java.io :refer :all]
            [clojure.pprint :refer [pprint]]
            [clj-commons-exec :as exec]))

(defn execute [command-str & {:keys [block?] :or {block? false}}]
  (let [commands (split command-str #" ")
        err (java.io.ByteArrayOutputStream.)
        out (java.io.ByteArrayOutputStream.)
        command (if block?
                  @(exec/sh commands {:out out :err err})
                  (exec/sh commands {:out out :err err}))]
    {:out out :err err :command command}))



(defrecord Command [setup command teardown started-check ready?]
  component/Lifecycle

  (start [comp]
    (println ";; Starting command")
    (let [setup (:setup comp)
          command (:command comp)
          started-check (:started-check comp) 
          setup-res (when (seq setup)
                      (execute setup :block? true))
          command-res (when (seq setup)
                        (execute command))
          started-res (when (seq setup)
                        (execute started-check :block? true))
          comp (if (zero? (get-in started-res [:command :exit]))
                      (assoc comp :ready? true)
                      (assoc comp :ready? false))
          comp (assoc comp :setup-res setup-res)
          comp (assoc comp :command-res command-res)
          comp (assoc comp :started-res started-res)]
      comp))

 
  (stop [comp]
    (println ";; Stopping command")
    (let [finished-check (:started-check comp) 
          teardown (:teardown comp)
          teardown-res (when (seq teardown)
                         (execute teardown :block? true))
          finished-res (when (seq finished-check)
                         (execute finished-check :block? true))
          comp (assoc comp :teardown-res teardown-res)
          comp (assoc comp :finished-res finished-res)
          comp (assoc comp :ready? false)]
      comp))

  serializer/Serialize
  (->json [this]
    (let [setup-result (when (:setup-res this)
                         {:out (.toString (get-in this [:setup-res :out])) 
                          :err (.toString (get-in this [:setup-res :err]))})
          command-result (when (:command-res this)
                         {:out (.toString (get-in this [:command-res :out])) 
                          :err (.toString (get-in this [:command-res :err]))})
          started-result (when (:started-res this)
                         {:out (.toString (get-in this [:started-res :out])) 
                          :err (.toString (get-in this [:started-res :err]))})
          teardown-result (when (:teardown-res this)
                         {:out (.toString (get-in this [:teardown-res :out])) 
                          :err (.toString (get-in this [:teardown-res :err]))})
          finished-result (when (:finished-res this)
                         {:out (.toString (get-in this [:finished-res :out])) 
                          :err (.toString (get-in this [:finished-res :err]))})
          result {:setup (:setup this)
                  :setup-result setup-result
                  :command (:command this)
                  :command-result command-result
                  :started-check (:started-check this)
                  :started-check-result started-result
                  :teardown (:teardown this)
                  :teardown-result teardown-result
                  :finished (:started-check this)
                  :finished-output finished-result
                  }]
      result)))


(defn build-command [setup command started-check teardown]
  (map->Command {:setup setup :command command :started-check started-check :teardown teardown :ready? false}))


(defn value->command [values]
  (let [setup (:setup values)
        command (:command values)
        started-check (:started-check values)
        teardown (:teardown values)]
    (build-command setup command started-check teardown)))

