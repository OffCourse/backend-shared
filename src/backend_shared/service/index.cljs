(ns backend-shared.service.index
  (:require [backend-shared.service.fetch :as fetch]
            [backend-shared.service.perform :as perform]
            [shared.protocols.queryable :refer [Queryable]]
            [shared.protocols.actionable :refer [Actionable]]
            [backend-shared.kinesis.index :as kinesis]
            [backend-shared.es.index :as es]
            [backend-shared.auth0.index :as auth0]
            [backend-shared.github.index :as github]
            [backend-shared.iam.index :as iam]
            [backend-shared.dynamodb.index :as dynamodb]
            [backend-shared.http.index :as http]
            [backend-shared.embedly.index :as embedly]
            [backend-shared.s3.index :as s3]
            [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]))

(def config (.config (node/require "dotenv")))
(def stage (.. js/process -env -SERVERLESS_STAGE))

(def constructors {:db        #(dynamodb/create)
                   :http      #(http/create)
                   :embedly   #(embedly/create)
                   :auth      #(auth0/create)
                   :iam       #(iam/create)
                   :index     #(es/create)
                   :github    #(github/create)
                   :bucket    #(s3/create)
                   :stream    #(kinesis/create)})

(defrecord Service []
  Actionable
  (-perform [service payload] (perform/perform service payload))
  Queryable
  (-fetch [service query]
    (fetch/fetch service query)))

(defn adapters [adapter-names]
  (reduce (fn [acc val] (assoc acc val ((val constructors))))
          {} adapter-names))

(defn create [name cb adapter-names mappings specs]
  (do
    (specs)
    (mappings)
    (map->Service (merge {:service-name name
                          :stage        stage
                          :callback     cb}
                         (adapters adapter-names)))))


(defn res [code body] {:statusCode code
                           :headers {:Access-Control-Allow-Origin "*"}
                           :body body})

(defn accepted
  ([{:keys [callback]}] (callback nil (clj->js (res 202 nil))))
  ([{:keys [callback]} payload] (callback nil (clj->js (res 202 payload)))))

(defn unauthorized [{:keys [callback]} error] (callback (str "[401] " error)))

(defn done [{:keys [callback]} payload] (callback nil (clj->js payload)))
(defn fail [{:keys [callback]} error] (callback (clj->js error)) nil)


(def fetch fetch/fetch)
(def perform perform/perform)
