(ns backend-adapters.dynamodb.perform
  (:require [cljs.core.async :as async]
            [shared.protocols.loggable :as log]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn -save [{:keys [instance] :as adapter} query]
  (let [c (async/chan)]
    (.put instance (clj->js query)
          #(let [response (if %1
                            (do
                              (log/log "ERROR: " (.stringify js/JSON %1))
                              [:error query])
                            [:success %2])]
             (when (= :error response)
               (log/log "Error Saving Item: " query))
             (async/put! c response)
             (async/close! c)))
    c))

(defn perform [{:keys [table-names] :as this} [_ payload]]
  (go
    (let [payload       (payload/create (into [] payload))
          queries       (cv/to-db payload table-names)
          payload-chans (async/merge (map #(-save this %) queries))
          res           (async/<! (async/into [] payload-chans))
          errors        (filter (fn [[result data]] (= :error result)) res)]
      (if (empty? errors)
        {:success true}
        {:error (map second errors)}))))
