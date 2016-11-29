(ns backend-adapters.index
  (:require [backend-adapters.auth0.index :as auth0]
            [backend-adapters.dynamodb.index :as dynamodb]
            [backend-adapters.embedly.index :as embedly]
            [backend-adapters.es.index :as es]
            [backend-adapters.github.index :as github]
            [backend-adapters.http.index :as http]
            [backend-adapters.iam.index :as iam]
            [backend-adapters.kinesis.index :as kinesis]
            [backend-adapters.s3.index :as s3]))


(def constructors {:db        #(dynamodb/create %)
                   :http      #(http/create %)
                   :embedly   #(embedly/create %)
                   :auth      #(auth0/create %)
                   :iam       #(iam/create %)
                   :index     #(es/create %)
                   :github    #(github/create %)
                   :bucket    #(s3/create %)
                   :stream    #(kinesis/create %)})
