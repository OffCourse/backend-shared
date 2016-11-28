(ns backend-shared.adapters.dynamodb.index
  (:require [cljs.nodejs :as node]
            [backend-shared.adapters.dynamodb.fetch :refer [fetch]]
            [backend-shared.adapters.dynamodb.perform :refer [perform]]
            [shared.protocols.queryable :refer [Queryable]]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.models.action.index :as action]
            [backend-shared.adapters.dynamodb.to-action :refer [to-action]]
            [backend-shared.adapters.dynamodb.to-query :refer [to-query]]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))

(defn create []
  (specify! (AWS.DynamoDB.DocumentClient.)
    Queryable
    (-fetch [table query] (fetch table query))
    Actionable
    (-perform [table action] (perform table action))))

(extend-protocol Convertible
  ;; This should be Action (still have to create a proper type for this...)
  PersistentVector
  (-to-db       [obj] (->  obj action/create to-action))
  ;; These should both be Query (still have to create a proper type for this...)
  PersistentHashMap
  (-to-db       [obj] (->  obj to-query))
  PersistentArrayMap
  (-to-db       [obj] (->  obj to-query)))
