(ns backend-adapters.es.fetch
  (:require [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [cuerdas.core :as str])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def ^:private js-request (node/require "request"))
(defn to-js [obj] (.parse js/JSON obj))

(defn request [config]
  (let [c (async/chan)]
    (js-request (clj->js config)
                (fn [error response body]
                  (async/put! c
                              (if error {:error :invalid-request} body)
                              #(async/close! c))))
    c))

(defn -fetch [endpoint-url index-name query]
  (go
    (if endpoint-url
      (<! (request {:url  (str endpoint-url "/offcourse/" index-name "/_search")
                    :body (.stringify js/JSON (clj->js query))}))
      {:error "elasticsearch endpoint needs to be set in the environment"})))

(defn extract-items [res]
  (->> res
       to-js
       cv/to-clj
       :hits :hits
       (mapv :_source)))

(defn fetch [{:keys [url] :as this} index-name query]
  (go
    (let [{:keys [error] :as res}      (async/<! (-fetch url index-name query))
          items                        (when-not error (extract-items res))]
      {:error error
       :found items})))
