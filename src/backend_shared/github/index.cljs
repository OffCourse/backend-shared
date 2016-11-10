(ns backend-shared.github.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.github.fetch :refer [fetch]]))

(defn create []
  (specify {:name :github
            :endpoint "http://api.github.com"}
    Queryable
    (-fetch [this query] (fetch this query))))
