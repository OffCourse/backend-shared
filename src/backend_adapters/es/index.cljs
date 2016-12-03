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

(defn create [{:keys [index-names search-url]}]
  (specify! {:name "elasticsearch"
             :endpoint-url search-url
             :endpoint (when search-url (AWS.Endpoint. search-url))
             :url search-url}
    Queryable
    (-fetch [this query] (fetch this query))
    Actionable
    (-perform [this action] (perform this action))))

(extend-protocol Convertible
  PersistentArrayMap
  (-to-search [obj] (to-query obj))
  PersistentVector
  (-to-search [obj] (to-payload obj)))
