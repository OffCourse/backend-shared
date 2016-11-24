(ns backend-shared.aws-event.index
  (:require [shared.protocols.convertible :as cv :refer [Convertible]]
            [backend-shared.aws-event.to-query :refer [to-query]]
            [backend-shared.aws-event.to-action :refer [to-action]]
            [backend-shared.aws-event.to-credentials :refer [to-credentials]]
            [backend-shared.aws-event.to-payload :refer [to-payload]]
            [cljs.nodejs :as node]))

(def marshaler (node/require "dynamodb-marshaler"))
(def unmarshal-item (.-unmarshalItem marshaler))

(defn extract-event [{:keys [eventName dynamodb]}]
  (case eventName
    "REMOVE" [:removed dynamodb]
    "INSERT" [:added (-> dynamodb :NewImage clj->js unmarshal-item cv/to-clj)]))

(defn group-db-event [acc record]
  (let [[event-type event] (extract-event record)]
    (update acc event-type conj event)))

(defrecord AwsEvent []
  Convertible
  (-to-credentials [this] (to-credentials this))
  (-to-query [this] (to-query this))
  (-to-action [this] (to-action this))
  (-to-events [{:keys [Records] :as this}] (reduce group-db-event {} Records))
  (-to-payload [this] (to-payload this)))

(defn create [raw-event]
  (-> raw-event
      cv/to-clj
      map->AwsEvent
      (with-meta {:spec :aws/event})))
