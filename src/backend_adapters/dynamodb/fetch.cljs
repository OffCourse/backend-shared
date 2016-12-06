(ns backend-adapters.dynamodb.fetch
  (:refer-clojure :exclude [get])
  (:require [cljs.core.async :as async]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [cljs.spec :as spec]
            [backend-adapters.dynamodb.to-query :refer [to-query]]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp])
  (:require-macros [cljs.core.async.macros :refer [go]]))

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

(spec/def ::single-or-multiple (spec/or :single map?
                                        :multiple (spec/coll-of map?)))

(defn exec-query [instance query]
  (let [c (async/chan)]
    (.query instance (clj->js query) #(handle-response c %1 %2 query))
    c))

(defmulti fetch (fn [_ query] (first (spec/conform ::single-or-multiple query))))

(defmethod fetch :single [{:keys [instance table-names] :as adapter} query]
  (let [query (to-query query table-names)]
    (exec-query instance query)))

(defmethod fetch :multiple [{:keys [instance table-names] :as adapter} query]
  (go
    (let [queries (to-query query table-names)
          query-chans (async/merge (map #(exec-query instance %1) queries))
          res (async/<! (async/into [] query-chans))]
      {:found (flatten (keep :found res))
       :errors (flatten (keep :error res))})))
