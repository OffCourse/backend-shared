(ns backend-adapters.dynamodb.to-action
  (:require [clojure.walk :as walk]
            [shared.models.action.index :as action]
            [shared.protocols.specced :as sp]))

(defn replaceEmptyStrings [obj]
  (walk/postwalk-replace {"" nil} obj))

(defn create-item [data table-name]
  {:TableName table-name
   :Item (-> data replaceEmptyStrings clj->js)})

(defn to-action [[_ payload :as action] table-names]
  (let [[action-type payload-type] (sp/resolve action)
        table-name (payload-type table-names)
        items (map #(create-item %1 table-name) payload)]
    (action/create [action-type items])))
