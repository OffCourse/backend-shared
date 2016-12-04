(ns backend-adapters.auth0.index
  (:require [backend-adapters.auth0.perform :refer [perform]]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.loggable :as log]))

(defn create [{:keys [api-keys] :as stage}]
  (specify {:name "auth0"
            :secret (:auth0 api-keys)}
    Actionable
    (-perform [this credentials] (perform this credentials))))
