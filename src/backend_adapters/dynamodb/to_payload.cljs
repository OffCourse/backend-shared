(ns backend-adapters.dynamodb.to-payload
  (:require [clojure.walk :as walk]
            [shared.models.action.index :as action]
            [shared.protocols.specced :as sp]))

(defn replaceEmptyStrings [obj]
  (walk/postwalk-replace {"" nil} obj))

(defn create-item [data table-name]
  {:TableName table-name
   :Item (-> data replaceEmptyStrings clj->js)})

(defn to-payload [payload table-names]
  (let [payload-type (sp/resolve payload)
        table-name (payload-type table-names)]
    (map #(create-item %1 table-name) payload)))
