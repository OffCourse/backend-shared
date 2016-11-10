(ns backend-shared.s3.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.s3.perform :refer [perform]]
            [backend-shared.s3.fetch :refer [fetch]]
            [shared.protocols.actionable :refer [Actionable]]
            [cljs.nodejs :as node]
            [shared.protocols.queryable :as qa]))

(def AWS (node/require "aws-sdk"))

(defn create []
  (specify! (new AWS.S3)
    Queryable
    (-fetch [this query] (fetch this query))
    Actionable
    (-perform [this action] (perform this action))))
