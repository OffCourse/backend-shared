(ns backend-shared.aws-event.to-payload
  (:require [cljs.spec :as spec]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [cljs.nodejs :as node]
            [cuerdas.core :as str]))

(def marshaler (node/require "dynamodb-marshaler"))
(def unmarshal-item (.-unmarshalItem marshaler))

(defn convert-buffer [data]
  (cv/to-clj (.toString (js/Buffer. data "base64") "ascii")))

(defmulti extract-record
  (fn [record]
    (first (spec/conform :aws/record record))))

(defn extract-image [image]
  (some-> image clj->js unmarshal-item cv/to-clj))

(defmethod extract-record :dynamodb [{:keys [eventName dynamodb]}]
  {:event-type (-> eventName str/dasherize keyword)
   :new-image (-> dynamodb :NewImage extract-image)
   :old-image (-> dynamodb :OldImage extract-image)})

(defmethod extract-record :s3 [{:keys [s3]}]
  (-> s3 :object :key))

(defmethod extract-record :kinesis [{:keys [kinesis]}]
  (-> kinesis :data convert-buffer))

(defmulti to-payload (fn [aws-event] (sp/resolve aws-event)))

(defmethod to-payload :code-pipeline [event]
  {:jobId (-> event :CodePipeline.job :id)})

(defmethod to-payload :auth [{:keys [methodArn authorizationToken]}]
  {:auth-token authorizationToken
   :method-arn methodArn})

(defmethod to-payload :stream [aws-event]
  (->> aws-event
       :Records
       (map extract-record)
       (flatten)
       (into [])
       payload/create))
