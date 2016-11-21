(ns backend-shared.auth0.perform
  (:require [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]))

(def jwt  (node/require "jsonwebtoken"))

(defn perform [{:keys [secret]} [_ {:keys [authorizationToken]}]]
  (let [c  (async/chan)]
    (if secret
      (.verify jwt authorizationToken (js/Buffer. secret "base64")
               #(async/put! c {:error (when %1 (log/pipe "AUTH ERROR" (clj->js %1)))
                               :auth-id (when %2 (:sub (cv/to-clj %2)))}))
      (async/put! c {:error "auth secret should be set in the environment"}))
    c))
