(ns backend-adapters.dynamodb.to-query
  (:require [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.query.index :as query]
            [shared.protocols.loggable :as log]))

(defmulti to-query sp/resolve)

(defmethod to-query :identity [query table-name]
  (query/create {:table-name table-name
                 :item-key (select-keys query [:auth-id])}))
