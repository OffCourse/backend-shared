(ns backend-shared.service.index
  (:require [backend-shared.aws-event.index :as aws-event]
            [backend-adapters.index :as adapters]
            [backend-shared.service.fetch :as fetch]
            [backend-shared.service.perform :as perform]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :refer [Queryable]]
            [clojure.walk :as walk]))

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

(defrecord Service []
  Actionable
  (-perform [service payload] (perform/perform service payload))
  Queryable
  (-fetch [service query] (fetch/fetch service query)))

(defn initialize [{:keys [specs mappings callback event context environment adapters] :as config}])

;; error handling needs to be much better here

(defn check [[key val]] (if-not val key (when (map? val) (keep check val))))

(defn create-adapters [adapters cb]
  (reduce (fn [acc [adapter-name config]]
            (let [errors (flatten (keep check config))]
              (if-not (empty? errors)
                (cb (clj->js (map #(str "envvar for " (name %) " not set") errors)) nil)
                (assoc acc adapter-name ((adapter-name adapters/constructors) config)))))
            {} adapters))

(defn create [specs mappings adapters event context callback]
  (specs)
  (mappings)
  #_(log-incoming event context)
  (map->Service (merge {:stage (.. js/process -env -serverlessStage)
                        :callback callback
                        :context (cv/to-clj context)
                        :event   (aws-event/create event)}
                       (create-adapters adapters callback))))

(defn res [code body] {:statusCode code
                       :headers {:Access-Control-Allow-Origin "*"}
                       :body body})

(defn accepted
  ([{:keys [callback]}] (callback nil (clj->js (res 202 nil))))
  ([{:keys [callback]} payload] (callback nil (clj->js (res 202 payload)))))

(defn unauthorized [{:keys [callback]} error] (callback (str "[401] " error)))

(defn done [{:keys [callback] :as s} payload] (callback nil (clj->js payload)))

(defn fail [{:keys [callback]} error] (callback (clj->js error)) nil)

(def fetch fetch/fetch)
(def perform perform/perform)
