(ns backend-shared.dynamodb.fetch
  (:refer-clojure :exclude [get])
  (:require [cljs.core.async :as async]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [cljs.spec :as spec]))

(defn create-query [{:keys [table-name item-key]}]
  (clj->js {:TableName table-name
            :Key       item-key}))

(defn response-params [error data query]
  (let [data (js->clj data :keywordize-keys true)]
    {:error     error
     :found     (when-not (empty? data) (payload/create (or (-> data cv/to-clj :Item)
                                                            (-> data cv/to-clj :Items))))
     :not-found (when (empty? data) query)}))

(defn handle-response [channel error data query]
  (async/put! channel (response-params error data query))
  (async/close! channel))

(defn handle-batch-response [channel error data query table-name]
  (let [table-name (keyword table-name)
        data (-> data cv/to-clj :Responses table-name)]
  (async/put! channel {:error error
                       :found     (when-not (empty? data) (payload/create data))
                       :not-found (when (empty? data) query)}))
  (async/close! channel))

(defn batchGet [table table-name query]
  (let [c (async/chan)]
    (.batchGet table
               (clj->js {:RequestItems {table-name {:Keys query}}})
               #(handle-batch-response c %1 %2 query table-name))
    c))

#_(defmulti fetch (fn [_ query] nil))

(defn fetch [table query]
  (let [c (async/chan)]
    (.get table (create-query query) #(handle-response c %1 %2 query))
    c))

#_(defmethod fetch :multiple [table table-name query]
  (batchGet table table-name query))
