(ns backend-shared.adapters.dynamodb.to-query
  (:require [cljs.nodejs :as node]
            [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.query.index :as query]
            [shared.protocols.loggable :as log]))

(def deployment-stage (.. js/process -env -SERVERLESS_STAGE))

(defmulti to-query sp/resolve)

(defmethod to-query :identity [query]
  (query/create {:table-name (str "identities-" deployment-stage)
                 :item-key (select-keys query [:auth-id])}))