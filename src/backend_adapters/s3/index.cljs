(ns backend-adapters.s3.index
  (:require [backend-adapters.s3.fetch :refer [fetch]]
            [backend-adapters.s3.perform :refer [perform]]
            [backend-adapters.s3.to-action :refer [to-action]]
            [backend-adapters.s3.to-query :refer [to-query]]
            [cljs.nodejs :as node]
            [shared.models.action.index :as action]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.protocols.queryable :refer [Queryable]]))

(def AWS (node/require "aws-sdk"))

(defn create [{:keys [table-names]}]
  (specify! {:instance (new AWS.S3)
             :table-names table-names}
    Queryable
    (-fetch [this query] (fetch this query))
    Actionable
    (-perform [this action] (perform this action))))

;; This should be Action (still have to create a proper type for this...)
(extend-protocol Convertible
  PersistentArrayMap
  (-to-bucket   [obj] (to-query obj))
  PersistentVector
  (-to-bucket   [obj bucket-name] (->  obj action/create (to-action bucket-name))))

