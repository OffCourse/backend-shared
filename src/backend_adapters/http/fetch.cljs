(ns backend-adapters.http.fetch
  (:require [shared.protocols.loggable :as log]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handle-response [c url error res]
  (if error
    (async/put! c [:error [url error]])
    (async/put! c [:found [url res]]))
  (async/close! c))

(defn -fetch [adapter url]
  (let [options {:uri url
                 :encoding nil}
        c (async/chan)]
    (adapter (clj->js options) #(handle-response c url %1 %3))
    c))

(defn fetch [adapter {:keys [urls]}]
  (go
    (let [query-chans (async/merge (map #(-fetch adapter %) urls))
          res         (async/<! (async/into [] query-chans))
          sorted      (reduce
                       (fn [acc [res-type [url data]]]
                         (assoc-in acc [res-type url] data)) {} res)]
      sorted)))
