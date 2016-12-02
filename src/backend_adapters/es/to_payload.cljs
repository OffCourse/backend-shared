(ns backend-adapters.es.to-payload
  (:require [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [shared.models.payload.index :as payload]))

(defmulti to-item (fn [item index-name] (sp/resolve (payload/create item))))

(defmethod to-item :resource [{:keys [resource-url] :as item} index-name]
  {:id resource-url
   :item item
   :index-name index-name})

(defmethod to-item :course [{:keys [course-id] :as item} index-name]
  {:id course-id
   :item item
   :index-name index-name})

(defmethod to-item :profile [{:keys [user-name] :as item} index-name]
  {:id user-name
   :item item
   :index-name index-name})

(defn to-payload [payload _]
  (let [index-name (sp/resolve (payload/create payload))]
    (map #(to-item %1 index-name) payload)))
