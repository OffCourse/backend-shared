(ns backend-adapters.dynamodb.perform
  (:require [cljs.core.async :as async]
            [clojure.walk :as walk]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn replaceEmptyStrings [obj]
  (walk/postwalk-replace {"" nil} obj))

(defn marshal [item]
  (-> item
      replaceEmptyStrings
      clj->js))

(defn create-query [{:keys [table-name item]} stage]
  {:TableName (str table-name "-" stage)
   :Item (marshal item)})

(defn -save [{:keys [action] :as adapter} query]
  (let [c (async/chan)]
    (.put action (clj->js query)
          #(let [response (if %1
                            (do
                              (log/log "ERROR: " %1)
                              [:error query])
                            [:success %2])]
             (when (= :error response)
               (log/log "Error Saving Item: " query))
             (async/put! c response)
             (async/close! c)))
    c))

(defn perform [{:keys [stage] :as this} [_ payload]]
  (go
    (let [queries (map #(create-query %1 stage) payload)
          query-chans (async/merge (map #(-save this %) queries))
          res         (async/<! (async/into [] query-chans))
          errors      (filter (fn [[result data]] (= :error result)) res)]
      (if (empty? errors)
        {:success true}
        {:error (map second errors)}))))