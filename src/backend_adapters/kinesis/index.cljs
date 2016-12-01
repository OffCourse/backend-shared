(ns backend-adapters.kinesis.index
  (:require [backend-adapters.kinesis.perform :refer [perform]]
            [backend-adapters.kinesis.to-payload :refer [to-payload]]
            [cljs.nodejs :as node]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv :refer [Convertible]]))

(def AWS (node/require "aws-sdk"))

(defn create [{:keys [stream-names] :as config}]
  (specify! {:instance (new AWS.Kinesis)
             :stream-names stream-names}
    Actionable
    (-perform [this action] (perform this action))))

(extend-protocol Convertible
  ;; This should be payload (still have to create a proper type for this...)
  PersistentVector
  (-to-stream [records stream-names] (to-payload records stream-names)))
