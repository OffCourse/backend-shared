(ns backend-shared.auth0.perform
  (:require [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]))

(def jwt  (node/require "jsonwebtoken"))

(defn perform [{:keys [secret]} [_ {:keys [authorizationToken] :as I}]]
  (let [c  (async/chan)]
    (.verify jwt authorizationToken secret
             #(async/put! c {:error (when %1 (log/pipe "AUTH ERROR" (clj->js %1)))
                             :auth-id (when %2 (:sub (cv/to-clj %2)))}))
    c))
