(ns backend-adapters.kinesis.to-payload
  (:require [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]))

(defn create-record [item]
  {:Data (.stringify js/JSON (clj->js item))
   :PartitionKey (str (rand-int 1000))})

(defn to-payload [records stream-names]
  (let [payload-type (sp/resolve records)
        stream-name (payload-type stream-names)]
    (log/log "X" payload-type)
    {:StreamName stream-name
     :Records (map create-record records)}))
