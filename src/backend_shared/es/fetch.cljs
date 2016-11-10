(ns backend-shared.es.fetch
  (:require [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [cuerdas.core :as str])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def endpoint-url (.. js/process -env -ELASTICSEARCH_ENDPOINT))
(def ^:private js-request (node/require "request"))
(defn to-js [obj] (.parse js/JSON obj))

(defn request [url-or-opts]
  (let [c (async/chan 1)]
    (js-request (clj->js url-or-opts)
                (fn [error response body]
                  (async/put! c
                              (if error {:error error} body)
                              #(async/close! c))))
    c))

(defn -fetch [index-name query]
  (go
    (<! (request {:url  (str endpoint-url "/offcourse/" index-name "/_search")
                  :body (.stringify js/JSON (clj->js query))}))))

(defn extract-items [res]
  (->> res to-js
       cv/to-clj
       :hits :hits
       (map :_source)))

(defn fetch [{:keys [endpoint] :as this} index-name query]
  (go
    (let [es-query (cv/to-es-query query)
          res      (async/<! (-fetch index-name es-query))
          items    (extract-items res)]
      {:found (if (= (sp/resolve query) :course)
                (first items)
                items)})))
