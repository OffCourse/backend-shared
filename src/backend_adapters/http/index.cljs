(ns backend-adapters.http.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-adapters.http.fetch :refer [fetch]]
            [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]))

(def -request (node/require "request"))
(def request (.defaults -request
                        (clj->js {:headers {:user-agent    "offcourse"}})))

(defn create [stage]
  (specify! request
    Queryable
    (-fetch [this query] (fetch this query))))
