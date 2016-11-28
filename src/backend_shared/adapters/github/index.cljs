(ns backend-shared.adapters.github.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.adapters.github.fetch :refer [fetch]]))

(defn create []
  (specify {:name :github
            :endpoint "http://api.github.com"}
    Queryable
    (-fetch [this query] (fetch this query))))
