(ns backend-shared.aws-event.to-query
  (:require [cljs.spec :as spec]
            [shared.specs.aws :as aws-specs]
            [shared.models.query.index :as query]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]))

(defn convert-buffer [data]
  (-> js/JSON
      (.parse (.toString (js/Buffer. data "base64") "ascii"))
      cv/to-clj))

(defmulti extract-record
  (fn [record]
    (first (spec/conform ::aws-specs/record record))))

(defmethod extract-record :s3 [{:keys [s3]}]
  {:item-key (-> s3 :object :key)
   :bucket-name (-> s3 :bucket :name)})

(defmethod extract-record :kinesis [{:keys [kinesis]}]
  (-> kinesis :data convert-buffer))

(defmulti to-query (fn [aws-event]
                     (sp/resolve aws-event)))

(defmethod to-query :api [aws-event]
  (-> aws-event :body query/create))

(defmethod to-query :stream [aws-event]
  (->> aws-event
       :Records
       (map extract-record)
       flatten
       query/create))

(defmethod to-query :auth [{:keys [auth-id]}]
  {:auth-id auth-id})
