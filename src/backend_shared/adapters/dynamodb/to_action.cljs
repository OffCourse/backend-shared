(ns backend-shared.adapters.dynamodb.to-action
  (:require [cljs.nodejs :as node]
            [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.action.index :as action]))

(def config (.config (node/require "dotenv")))
(def service-name (.. js/process -env -SERVERLESS_SERVICE_NAME))
(def deployment-stage (.. js/process -env -SERVERLESS_STAGE))

(defn create-item [table-name data]
  {:table-name (str #_service-name #_"-" (name table-name) "-" deployment-stage)
   :item data})

(defmulti to-action sp/resolve)

(defmethod to-action :default [[_ payload :as action]]
  (let [[action-type table-name] (sp/resolve action)
        items (map #(create-item table-name %1) payload)]
    (action/create [action-type items])))
