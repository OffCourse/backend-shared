(ns backend-shared.adapters.kinesis.perform
  (:require [cljs.core.async :as async :refer [>! chan]]
            [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]
            [shared.specs.helpers :as sh]
            [shared.protocols.specced :as sp])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))
(def kinesis (new AWS.Kinesis))

(defn create-record [item]
  {:Data (.stringify js/JSON (clj->js item))
   :PartitionKey (str (rand-int 1000))})

(defn response-params [error data channel query]
  {:error error
   :data  data
   :channel channel
   :query query})

(defn handle-response [{:keys [error data channel query] :as res}]
  (if error
    (do
      (log/log "ERROR:" (clj->js error))
      (async/put! channel {:error {:data query
                                   :explanation error}}))
    (async/put! channel {:accepted (js->clj data :keywordize-keys true)}))
  (async/close! channel))

(defn -send [message]
  (let [c (async/chan)]
    (.putRecords kinesis (clj->js message)
                 #(handle-response (response-params %1 %2 c message)))
    c))

(defn create [stream-name items]
  {:StreamName stream-name
   :Records (map create-record items)})

(defn perform [stream [_ {:keys [stream-name records]}]]
  (let [message (create stream-name records)]
    (-send message)))
