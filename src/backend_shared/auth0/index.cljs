(ns backend-shared.auth0.index
  (:require [shared.protocols.actionable :refer [Actionable]]
            [cljs.nodejs :as node]
            [cljs.core.async :as async]
            [backend-shared.auth0.perform :refer [perform]]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def api-secret (.. js/process -env -authSecret))

(defn create []
  (specify {:name "auth0"
            :secret api-secret}
    Actionable
    (-perform [this credentials] (perform this credentials))))
