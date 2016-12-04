(ns backend-adapters.github.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-adapters.github.fetch :refer [fetch]]
            [shared.protocols.loggable :as log]))

(defn create [{:keys [api-keys]}]
  (specify {:name :github
            :api-key (:github api-keys)
            :endpoint "http://api.github.com"}
    Queryable
    (-fetch [this query] (fetch this query))))
