(ns backend-shared.adapters.http.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.adapters.http.fetch :refer [fetch]]
            [cljs.nodejs :as node]))

(def -request (node/require "request"))
(def request (.defaults -request
                        (clj->js {:headers {:user-agent    "offcourse"}})))

(defn create []
  (specify! request
    Queryable
    (-fetch [this query] (fetch this query))))
