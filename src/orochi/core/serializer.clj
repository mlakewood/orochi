(ns orochi.core.serializer
  (:require [cheshire.core :refer [generate-string]]))

(defprotocol Serialize
  (->json [this]))
