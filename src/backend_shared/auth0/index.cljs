(ns backend-shared.auth0.index
  (:require [shared.protocols.actionable :refer [Actionable]]
            [cljs.nodejs :as node]
            [cljs.core.async :as async]
            [backend-shared.auth0.perform :refer [perform]]
            [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def config (.config (node/require "dotenv")))
(def api-secret (.. js/process -env -AUTH0_SECRET))

(defn create []
  (specify {:name "auth0"
            :secret (js/Buffer. api-secret "base64")}
    Actionable
    (-perform [this credentials] (perform this credentials))))
