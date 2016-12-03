(ns backend-adapters.dynamodb.index
  (:require [cljs.nodejs :as node]
            [backend-adapters.dynamodb.fetch :refer [fetch]]
            [backend-adapters.dynamodb.perform :refer [perform]]
            [shared.protocols.queryable :refer [Queryable]]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.models.action.index :as action]
            [backend-adapters.dynamodb.to-payload :refer [to-payload]]
            [backend-adapters.dynamodb.to-query :refer [to-query]]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))

(defn create [{:keys [table-names]}]
  (specify {:instance (AWS.DynamoDB.DocumentClient.)
            :table-names  table-names}
    Queryable
    (-fetch [table query] (fetch table query))
    Actionable
    (-perform [table action] (perform table action))))

(extend-protocol Convertible
  ;; This should be Action (still have to create a proper type for this...)
  PersistentVector
  (-to-db       [obj table-names] (to-payload obj table-names))
  ;; These should both be Query (still have to create a proper type for this...)
  PersistentHashMap
  (-to-db       [obj table-names] (to-query obj table-names))
  PersistentArrayMap
  (-to-db       [obj table-names] (to-query obj table-names)))
