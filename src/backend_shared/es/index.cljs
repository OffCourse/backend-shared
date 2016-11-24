(ns backend-shared.es.index
  (:require [cljs.nodejs :as node]
            [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.es.fetch :refer [fetch]]
            [backend-shared.es.perform :refer [perform]]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.loggable :as log]))

(def AWS (node/require "aws-sdk"))
(def path (node/require "path"))

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
