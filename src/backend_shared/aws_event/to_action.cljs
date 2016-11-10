(ns backend-shared.aws-event.to-action
  (:require [shared.models.action.index :as action]))

(defn to-action [{:keys [body] :as this}]
  (action/create [(-> body :action-type keyword) (-> body :payload)]))
