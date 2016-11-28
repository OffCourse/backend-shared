(ns backend-shared.adapters.index
  (:require [backend-shared.adapters.auth0.index :as auth0]
            [backend-shared.adapters.dynamodb.index :as dynamodb]
            [backend-shared.adapters.embedly.index :as embedly]
            [backend-shared.adapters.es.index :as es]
            [backend-shared.adapters.github.index :as github]
            [backend-shared.adapters.http.index :as http]
            [backend-shared.adapters.iam.index :as iam]
            [backend-shared.adapters.kinesis.index :as kinesis]
            [backend-shared.adapters.s3.index :as s3]))

(def constructors {:db        #(dynamodb/create)
                   :http      #(http/create)
                   :embedly   #(embedly/create)
                   :auth      #(auth0/create)
                   :iam       #(iam/create)
                   :index     #(es/create)
                   :github    #(github/create)
                   :bucket    #(s3/create)
                   :stream    #(kinesis/create)})
