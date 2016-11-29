(ns backend-adapters.dynamodb.to-action
  (:require [cljs.nodejs :as node]
            [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.action.index :as action]))

(defn create-item [table-name data]
  {:table-name (name table-name)
   :item data})

(defmulti to-action sp/resolve)

(defmethod to-action :default [[_ payload :as action]]
  (let [[action-type table-name] (sp/resolve action)
        items (map #(create-item table-name %1) payload)]
    (action/create [action-type items])))
