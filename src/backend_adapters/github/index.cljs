(ns backend-adapters.github.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-adapters.github.fetch :refer [fetch]]))

(defn create [stage]
  (specify {:name :github
            :endpoint "http://api.github.com"}
    Queryable
    (-fetch [this query] (fetch this query))))
