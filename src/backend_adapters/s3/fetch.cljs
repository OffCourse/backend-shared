(ns backend-adapters.s3.fetch
  (:require [cljs.core.async :as async]
            [shared.protocols.loggable :as log]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def Unzip (node/require "jszip"))

(defn convert-payload [{:keys [Body] :as event}]
  (let [c (async/chan)]
    (if (.isBuffer js/Buffer Body)
      (do
        (.then (.loadAsync Unzip Body) #(async/put! c (cv/to-clj %1))))
      (async/put! c (cv/to-clj (.parse js/JSON (-> event :Body)))))
    c))


(defn to-payload [event]
  (-> event
      cv/to-clj
      convert-payload))

(defn- -get [{:keys [instance]} query]
  (let [c (async/chan)]
    (.getObject instance (clj->js query)
                #(go
                   (let [response
                         (if %1
                           {:error %1}
                           (async/<! (to-payload %2)))]
                     (when (= :error response)
                       (log/log "Error Saving Item: " query))
                     (async/put! c response)
                     (async/close! c))))
    c))

(defn fetch
  ([adapter query]
   (go
     (let [queries (map #(cv/to-bucket %1) query)
           query-chans (async/merge (map #(-get adapter %1) queries))
           merged-res  (async/<! (async/into [] query-chans))
           errors      (filter (fn [{:keys [error]}] error) merged-res)
           found       (remove (fn [{:keys [error]}] error) merged-res)]
       {:found found
        :errors errors})))
   ([{:keys [instance] :as adapter} credentials query]
    (.update instance.config (clj->js (assoc credentials :signatureVersion "v4")))
    (fetch adapter query)))
