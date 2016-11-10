(ns backend-shared.embedly.index
  (:require [shared.protocols.queryable :refer [Queryable]]
            [backend-shared.embedly.fetch :refer [fetch]]
            [cljs.nodejs :as node]))

(def config (.config (node/require "dotenv")))
(def api-key (.. js/process -env -EMBEDLY_API_KEY))

(defn create []
  (specify {:api-key api-key
            :api-version "1"
            :endpoint "http://api.embed.ly"}
    Queryable
    (-fetch [this query] (fetch this query))))
