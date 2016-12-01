(ns backend-adapters.s3.fetch
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

(defn- -get [{:keys [instance]} query]
  (let [c (async/chan)]
    (.getObject instance (clj->js query)
                #(let [response (if %1
                                  {:error %1}
                                  (to-payload %2))]
                   (when (= :error response)
                     (log/log "Error Saving Item: " query))
                   (async/put! c response)
                   (async/close! c)))
    c))

(defn fetch [{:keys [bucket-names] :as adapter} query]
  (go
    (let [queries (map #(cv/to-bucket %1) query)
          query-chans (async/merge (map #(-get adapter %1) queries))
          merged-res  (async/<! (async/into [] query-chans))
          errors      (filter (fn [{:keys [error]}] error) merged-res)
          found       (remove (fn [{:keys [error]}] error) merged-res)]
      {:found found
       :errors errors})))
