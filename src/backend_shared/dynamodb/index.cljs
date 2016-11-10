(ns backend-shared.dynamodb.index
  (:require [cljs.nodejs :as node]
            [backend-shared.dynamodb.fetch :refer [fetch]]
            [backend-shared.dynamodb.perform :refer [perform]]
            [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.dynamodb.to-action :as ta-impl]
            [backend-shared.dynamodb.to-query :as tq-impl]
            [shared.protocols.actionable :refer [Actionable]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))

(defn create []
  (specify! (AWS.DynamoDB.DocumentClient.)
    Queryable
    (-fetch [table query] (fetch table query))
    Actionable
    (-perform [table action] (perform table action))))

(def to-action ta-impl/to-action)
(def to-query tq-impl/to-query)
