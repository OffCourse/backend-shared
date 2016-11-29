(ns backend-adapters.kinesis.index
  (:require [backend-adapters.kinesis.perform :refer [perform]]
            [backend-adapters.kinesis.to-action :as kinesis]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.models.action.index :as action]
            [shared.protocols.actionable :refer [Actionable]]
            [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]))

(def AWS (node/require "aws-sdk"))

(defn create [stage]
  (specify! (new AWS.Kinesis)
    Actionable
    (-perform [this action] (perform this action))))

(extend-protocol Convertible
  ;; This should be Action (still have to create a proper type for this...)
  PersistentVector
  (-to-stream   [obj] (->  obj action/create kinesis/to-action)))