(ns backend-shared.adapters.embedly.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.adapters.embedly.fetch :refer [fetch]]
            [shared.protocols.loggable :as log]))

(def api-key (.. js/process -env -embedlyApiKey))

(defn create []
  (specify {:api-key (js->clj api-key)
            :api-version "1"
            :endpoint "http://api.embed.ly"}
    Queryable
    (-fetch [this query] (fetch this query))))
