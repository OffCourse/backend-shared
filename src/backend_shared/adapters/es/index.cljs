(ns backend-shared.adapters.es.index
  (:require [backend-shared.adapters.es.fetch :refer [fetch]]
            [backend-shared.adapters.es.perform :refer [perform]]
            [backend-shared.adapters.es.to-query :refer [to-query]]
            [cljs.nodejs :as node]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [shared.protocols.queryable :refer [Queryable]]))

(def AWS (node/require "aws-sdk"))
(def endpoint-url (.. js/process -env -elasticsearchEndpoint))
(def endpoint (when endpoint-url (AWS.Endpoint. endpoint-url)))

(defn create []
  (specify! {:name "elasticsearch"
             :endpoint endpoint
             :url endpoint-url}
    Queryable
    (-fetch [this index-name query] (fetch this index-name query))
    Actionable
    (-perform [this index-name action] (perform this index-name action))))

(extend-protocol Convertible
  PersistentArrayMap
  (-to-search [obj] (->  obj to-query)))
