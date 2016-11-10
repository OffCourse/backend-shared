(ns backend-shared.kinesis.index
  (:require [backend-shared.kinesis.perform :refer [perform]]
            [shared.protocols.actionable :refer [Actionable]]
            [cljs.nodejs :as node]))

(def AWS (node/require "aws-sdk"))

(defn create []
  (specify! (new AWS.Kinesis)
    Actionable
    (-perform [this action] (perform this action))))
