(ns backend-shared.aws-event.to-credentials
  (:require [shared.protocols.loggable :as log]))

(defn to-credentials [event]
  {:auth-token (-> event :authorizationToken)})
