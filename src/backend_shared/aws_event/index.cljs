(ns backend-shared.aws-event.index
  (:require [backend-shared.aws-event.to-action :refer [to-action]]
            [backend-shared.aws-event.to-credentials :refer [to-credentials]]
            [backend-shared.aws-event.to-db-events :refer [to-db-events]]
            [backend-shared.aws-event.to-payload :refer [to-payload]]
            [backend-shared.aws-event.to-query :refer [to-query]]
            [shared.protocols.convertible :as cv :refer [Convertible]]))

(defrecord AwsEvent []
  Convertible
  (-to-credentials [this] (to-credentials this))
  (-to-query [this]       (to-query this))
  (-to-action [this]      (to-action this))
  (-to-events [this]      (to-db-events this))
  (-to-payload [this]     (to-payload this)))

(defn create [raw-event]
  (-> raw-event
      cv/to-clj
      map->AwsEvent
      (with-meta {:spec :aws/event})))
