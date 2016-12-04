(ns backend-adapters.es.to-payload
  (:require [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [shared.models.payload.index :as payload]))

(defmulti to-item (fn [item index-name] index-name))

(defmethod to-item :resources [{:keys [resource-url] :as item} index-name]
  {:id resource-url
   :item item
   :index-name index-name})

(defmethod to-item :courses [{:keys [course-id] :as item} index-name]
  {:id course-id
   :item item
   :index-name index-name})

(defmethod to-item :profiles[{:keys [user-name] :as item} index-name]
  {:id user-name
   :item item
   :index-name index-name})

(defn to-payload [payload]
  (let [index-name (sp/resolve (payload/create payload))]
    (map #(to-item %1 index-name) payload)))
