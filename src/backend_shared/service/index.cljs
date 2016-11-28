(ns backend-shared.service.index
  (:require [backend-shared.aws-event.index :as aws-event]
            [backend-shared.adapters.index :as adapters]
            [backend-shared.service.fetch :as fetch]
            [backend-shared.service.perform :as perform]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :refer [Queryable]]))


(def stage (.. js/process -env -SERVERLESS_STAGE))

(defrecord Service []
  Actionable
  (-perform [service payload] (perform/perform service payload))
  Queryable
  (-fetch [service query] (fetch/fetch service query)))

(defn initialize-adapters [adapter-names]
  (reduce (fn [acc val] (assoc acc val ((val adapters/constructors))))
          {} adapter-names))

(defn log-incoming [event context]
  (log/log "")
  (log/log "---------------")
  (log/log "INCOMING EVENT: " event)
  (log/log "---------------")
  (log/log "")
  (log/log "---------------")
  (log/log "FUNCTION CONTEXT: " context)
  (log/log "---------------")
  (log/log ""))

(defn initialize [{:keys [specs mappings event context adapters] :as config}]
  (specs)
  (mappings)
  (log-incoming event context)
  (map->Service (merge config
                       {:stage stage
                        :context (cv/to-clj context)
                        :event   (aws-event/create event)}
                        (initialize-adapters adapters))))

(defn create [name cb adapter-names mappings specs]
  (do
    (specs)
    (mappings)
    (map->Service (merge {:service-name name
                          :stage        stage
                          :callback     cb}
                         (initialize-adapters adapter-names)))))


(defn res [code body] {:statusCode code
                           :headers {:Access-Control-Allow-Origin "*"}
                           :body body})

(defn accepted
  ([{:keys [callback]}] (callback nil (clj->js (res 202 nil))))
  ([{:keys [callback]} payload] (callback nil (clj->js (res 202 payload)))))

(defn unauthorized [{:keys [callback]} error] (callback (str "[401] " error)))

(defn done [{:keys [callback]} payload] (callback nil (clj->js payload)))
(defn fail [{:keys [callback]} error] (callback (clj->js error)) nil)
