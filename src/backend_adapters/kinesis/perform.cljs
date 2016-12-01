(ns backend-adapters.kinesis.perform
  (:require [cljs.core.async :as async :refer [>! chan]]
            [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]
            [shared.specs.helpers :as sh]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv]
            [shared.models.payload.index :as payload])
  (:require-macros [cljs.core.async.macros :refer [go]]))

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

(defn -send [{:keys [instance]} message]
  (let [c (async/chan)]
    (.putRecords instance (clj->js message)
                 #(handle-response (response-params %1 %2 c message)))
    c))

(defn perform [{:keys [stream-names] :as adapter} [_ payload]]
  (let [payload (payload/create (into [] payload))
        message (cv/to-stream payload stream-names)]
    (-send adapter message)))
