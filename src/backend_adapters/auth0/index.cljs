(ns backend-adapters.auth0.index
  (:require [shared.protocols.actionable :refer [Actionable]]
            [cljs.nodejs :as node]
            [cljs.core.async :as async]
            [backend-adapters.auth0.perform :refer [perform]]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def api-secret (.. js/process -env -authSecret))

(defn create [stage]
  (do
    (specify {:name "auth0"
              :stage stage
              :secret api-secret}
      Actionable
      (-perform [this credentials] (perform this credentials)))))
