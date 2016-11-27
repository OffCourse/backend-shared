(ns backend-shared.embedly.fetch
  (:require [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.models.payload.index :as payload]
            [shared.protocols.loggable :as log]
            [clojure.string :as str]
           [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def request (node/require "request"))

(defn create-url [endpoint api-version api-key urls]
  (str endpoint "/" api-version "/extract?key=" api-key "&urls=" (str/join "," urls)))

(defn handle-response [channel error res data]
  (let [status-code                    (aget res "statusCode")
        {:keys [error_code] :as body} (cv/to-clj data)
        error                         (when (or error error_code (>= status-code 400)) res)
        data                          (when-not error (map #(-> % cv/to-clj) body))]
    (async/put! channel {:error (when error error)
                         :found (when-not error data)})
    (async/close! channel)))

(defn fetch [{:keys [endpoint api-version api-key] :as this} urls]
  (if (= api-key "undefined")
    (go {:error "embedly api-key needs to be set in the environment"})
    (let [c (async/chan)
          url (create-url endpoint api-version api-key urls)
          res (request url #(handle-response c %1 %2 %3))]
      c)))
