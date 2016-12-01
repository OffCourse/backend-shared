(ns backend-adapters.s3.perform
  (:require [cljs.nodejs :as node]
            [cljs.core.async :as async]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv]
            [shared.models.payload.index :as payload])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn put [{:keys [instance] :as this} query]
  (let [c (async/chan)]
    (.putObject instance (clj->js query)
                #(let [response (if %1
                                  [:error %1]
                                  [:success %2])]
                   (when (= :error response)
                     (log/log "Error Saving Item: " query))
                   (async/put! c response)
                   (async/close! c)))
    c))

(defn create-query [{:keys [item-key bucket-name item-data]}]
  {:Bucket bucket-name
   :Key item-key
   :Body item-data})

(defn perform [{:keys [bucket-names] :as this} [_ payload :as action]]
  (go
    (let [payload     (payload/create (into [] payload))
          queries     (cv/to-bucket payload bucket-names)
          query-chans (async/merge (map #(put this %) queries))
          res         (async/<! (async/into [] query-chans))
          errors      (filter (fn [[result data]] (= :error result)) res)]
      (log/log "X" (clj->js queries))
      (if (empty? errors)
        {:success queries}
        {:error (map second errors)}))))
