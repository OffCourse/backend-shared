(ns backend-adapters.dynamodb.to-query
  (:require [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.query.index :as query]
            [shared.protocols.loggable :as log]))

(defmulti to-query (fn [query _] (sp/resolve query)))

(defmethod to-query :bookmarks [query table-names]
  (let [query-type (sp/resolve query)
        table-name (query-type table-names)]
    (query/create {:TableName table-name
                   :ExpressionAttributeNames {"#fn" "resource-url"}
                   :ExpressionAttributeValues {":val" (str (:resource-url (first query)))}
                   :KeyConditionExpression (str "#fn = :val")})))

(defmethod to-query :resource [query table-names]
  (let [query-type (sp/resolve query)
        table-name (query-type table-names)]
    (query/create {:TableName table-name
                   :Count 1
                   :ExpressionAttributeNames {"#fn" "resource-url"}
                   :ExpressionAttributeValues {":val" (str (:resource-url query))}
                   :KeyConditionExpression (str "#fn = :val")})))

(defmethod to-query :identity [query table-names]
  (let [query-type (sp/resolve query)
        table-name (query-type table-names)]
    (query/create {:TableName table-name
                   :Count 1
                   :ExpressionAttributeNames {"#fn" "auth-id"}
                   :ExpressionAttributeValues {":val" (str (:auth-id query))}
                   :KeyConditionExpression (str "#fn = :val")})))
