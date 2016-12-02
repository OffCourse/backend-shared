(ns backend-adapters.es.index
  (:require [backend-adapters.es.fetch :refer [fetch]]
            [backend-adapters.es.perform :refer [perform]]
            [backend-adapters.es.to-query :refer [to-query]]
            [backend-adapters.es.to-payload :refer [to-payload]]
            [cljs.nodejs :as node]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.protocols.queryable :refer [Queryable]]))

(def AWS (node/require "aws-sdk"))
(def endpoint-url (.. js/process -env -elasticsearchEndpoint))
(def endpoint (when endpoint-url (AWS.Endpoint. endpoint-url)))

(defn create [{:keys [index-names]}]
  (specify! {:name "elasticsearch"
             :endpoint endpoint
             :index-names index-names
             :url endpoint-url}
    Queryable
    (-fetch [this index-name query] (fetch this index-name query))
    Actionable
    (-perform [this action] (perform this action))))

(extend-protocol Convertible
  PersistentArrayMap
  (-to-search [obj index-names] (to-query obj index-names))
  PersistentVector
  (-to-search [obj index-names] (to-payload obj index-names)))
