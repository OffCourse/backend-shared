(ns backend-shared.aws-event.to-db-events
  (:require [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]))

(def marshaler (node/require "dynamodb-marshaler"))
(def unmarshal-item (.-unmarshalItem marshaler))

(defn extract-event [{:keys [eventName dynamodb]}]
  (case eventName
    "REMOVE" [:removed dynamodb]
    "MODIFY" [:added (-> dynamodb :NewImage clj->js unmarshal-item cv/to-clj)]
    "INSERT" [:added (-> dynamodb :NewImage clj->js unmarshal-item cv/to-clj)]))

(defn group-db-event [acc record]
  (let [[event-type event] (extract-event record)]
    (update acc event-type conj event)))

(defn to-db-events [{:keys [Records] :as this}]
  (reduce group-db-event {} Records))
