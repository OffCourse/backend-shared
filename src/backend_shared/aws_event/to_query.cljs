(ns backend-shared.aws-event.to-query
  (:require [cljs.spec :as spec]
            [shared.models.query.index :as query]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]))

(defn convert-buffer [data]
  (cv/to-clj (.toString (js/Buffer. data "base64") "ascii")))

(defmulti extract-record
  (fn [record]
    (first (spec/conform :aws/record record))))

(defmethod extract-record :s3 [{:keys [s3]}]
  {:item-key (-> s3 :object :key)
   :bucket-name (-> s3 :bucket :name)})

(defmethod extract-record :kinesis [{:keys [kinesis]}]
  (-> kinesis :data convert-buffer))

(defmulti to-query (fn [event] (sp/resolve event)))

(defmethod to-query :api [event]
  (-> event :body query/create))

(defn extract-secure-item [{:keys [location revision]} credentials]
  {:item-key (-> location :s3Location :objectKey)
   :bucket-name (-> location :s3Location :bucketName)
   :revision revision
   :credentials credentials})

(defn extract-secure-items [artifacts credentials]
  (map #(extract-secure-item %1 credentials) artifacts))

(defmethod to-query :code-pipeline [event]
  (let [{:keys [inputArtifacts outputArtifacts artifactCredentials]} (-> event :CodePipeline.job :data)]
    {:input-queries (extract-secure-items inputArtifacts artifactCredentials)
     :output-queries (extract-secure-items outputArtifacts artifactCredentials)}))

(defmethod to-query :stream [event]
  (->> event
       :Records
       (map extract-record)
       flatten
       query/create))

(defmethod to-query :auth [{:keys [auth-id]}]
  {:auth-id auth-id})
