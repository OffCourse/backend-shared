(ns backend-shared.convertible
  (:require [backend-shared.aws-event.index :as aws-event]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.protocols.specced :as sp]
            [backend-shared.s3.to-action :as s3]
            [backend-shared.dynamodb.index :as dynamodb]
            [backend-shared.es.to-query :as es]
            [backend-shared.kinesis.to-action :as kinesis]
            [shared.models.action.index :as action]))

(extend-protocol Convertible
  array
  (-to-clj [js-arr]             (js->clj js-arr :keywordize-keys true))
  object
  (-to-clj [js-obj]             (js->clj js-obj :keywordize-keys true))
  (-to-credentials [raw-event]  (-> raw-event aws-event/create cv/to-credentials))
  (-to-events [raw-event]       (-> raw-event aws-event/create cv/to-events))
  (-to-payload [raw-event]      (-> raw-event aws-event/create cv/to-payload))
  (-to-action [raw-event]       (-> raw-event aws-event/create cv/to-action))
  (-to-query [raw-event]        (-> raw-event aws-event/create cv/to-query))

  ;; This should be Action (still have to create a proper type for this...)
  PersistentVector
  (-to-db       [obj]           (->  obj action/create dynamodb/to-action))
  (-to-bucket   [obj]           (->  obj action/create s3/to-action))
  (-to-stream   [obj]           (->  obj action/create kinesis/to-action))

  ;; These should both be Query (still have to create a proper type for this...)
  PersistentHashMap
  (-to-db       [obj]           (->  obj dynamodb/to-query))
  (-to-json     [obj]           (->> obj clj->js (.stringify js/JSON)))
  PersistentArrayMap
  (-to-db       [obj]           (->  obj dynamodb/to-query))
  (-to-search   [obj]           (->  obj es/to-query))
  (-to-json     [obj]           (->> obj clj->js (.stringify js/JSON))))
