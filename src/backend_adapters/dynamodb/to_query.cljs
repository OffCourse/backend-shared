(ns backend-adapters.dynamodb.to-query
  (:require [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.query.index :as query]
            [shared.protocols.loggable :as log]))

(defmulti to-query (fn [query _] (sp/resolve query)))

(defmethod to-query :resource [query table-names]
  (let [query-type (sp/resolve query)
        table-name (query-type table-names)]
    (query/create {:TableName table-name
                   :Key (select-keys query [:resource-url])})))

(defmethod to-query :identity [query table-names]
  (let [query-type (sp/resolve query)
        table-name (query-type table-names)]
    (query/create {:TableName table-name
                   :Key (select-keys query [:auth-id])})))
