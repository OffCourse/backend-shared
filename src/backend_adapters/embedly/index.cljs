(ns backend-adapters.embedly.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-adapters.embedly.fetch :refer [fetch]]
            [shared.protocols.loggable :as log]))

(defn create [{:keys [api-keys] :as stage}]
  (specify {:api-key (:embedly api-keys)
            :api-version "1"
            :stage stage
            :endpoint "http://api.embed.ly"}
    Queryable
    (-fetch [this query] (fetch this query))))
