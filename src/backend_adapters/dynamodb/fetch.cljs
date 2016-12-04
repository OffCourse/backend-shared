(ns backend-adapters.dynamodb.fetch
  (:refer-clojure :exclude [get])
  (:require [cljs.core.async :as async]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [cljs.spec :as spec]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]))

(defn response-params [error data query]
  (let [data (cv/to-clj data)]
    {:error     error
     :found     (when-not (empty? data) (payload/create (or (-> data cv/to-clj :Item)
                                                            (-> data cv/to-clj :Items))))
     :not-found (when (empty? data) query)}))

(defn handle-response [channel error data query]
  (async/put! channel (response-params error data query))
  (async/close! channel))

(defn fetch [{:keys [instance table-names] :as adapter} query]
  (let [c (async/chan)
        query (cv/to-db query table-names)]
    (.get instance (clj->js query) #(handle-response c %1 %2 query))
    c))
