(ns backend-shared.adapters.s3.fetch
  (:require [cljs.core.async :as async]
            [shared.protocols.loggable :as log]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn convert-payload [event]
  (.parse js/JSON (-> event :Body)))

(defn to-payload [event]
  (-> event
      cv/to-clj
      convert-payload
      cv/to-clj))

(defn- -get [bucket query]
  (let [c (async/chan)]
    (.getObject bucket (clj->js query)
                #(let [response (if %1
                                  {:error %1}
                                  (to-payload %2))]
                   (when (= :error response)
                     (log/log "Error Saving Item: " query))
                   (async/put! c response)
                   (async/close! c)))
    c))

(defn create-query [{:keys [bucket-name item-key]}]
  {:Bucket bucket-name
   :Key item-key})

(defn fetch [bucket query]
  (go
    (let [queries (map #(create-query %1) query)
          query-chans (async/merge (map #(-get bucket %1) queries))
          merged-res  (async/<! (async/into [] query-chans))
          errors      (filter (fn [{:keys [error]}] error) merged-res)
          found       (remove (fn [{:keys [error]}] error) merged-res)]
      {:found found
       :errors errors})))
