(ns backend-adapters.embedly.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-adapters.embedly.fetch :refer [fetch]]
            [shared.protocols.loggable :as log]))

(def api-key (.. js/process -env -embedlyApiKey))

(defn create [stage]
  (specify {:api-key (js->clj api-key)
            :api-version "1"
            :stage stage
            :endpoint "http://api.embed.ly"}
    Queryable
    (-fetch [this query] (fetch this query))))
