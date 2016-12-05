(ns backend-adapters.dynamodb.fetch
  (:refer-clojure :exclude [get])
  (:require [cljs.core.async :as async]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [cljs.spec :as spec]
            [backend-adapters.dynamodb.to-query :refer [to-query]]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]))

(defn response-params [error data {:keys [Count] :as query}]
  (let [data (cv/to-clj data)]
    {:error     error
     :found     (when-not (empty? data)
                  (if (= Count 1)
                    (-> data cv/to-clj :Items first payload/create)
                    (-> data cv/to-clj :Items payload/create)))
     :not-found (when (empty? data) query)}))

(defn handle-response [channel error data query]
  (async/put! channel (response-params error data query))
  (async/close! channel))

(defn fetch [{:keys [instance table-names] :as adapter} query]
  (let [c (async/chan)
        query (to-query query table-names)]
    (.query instance (clj->js query) #(handle-response c %1 %2 query))
    c))
