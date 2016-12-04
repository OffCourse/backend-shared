(ns backend-adapters.s3.to-payload
  (:require [backend-adapters.s3.to-item :refer [to-item]]
            [shared.protocols.specced :as sp]
            [shared.models.payload.index :as payload]))

(defn to-payload [payload bucket-names]
  (let [payload-type (sp/resolve (payload/create payload))
        bucket-name (payload-type bucket-names)]
    (map #(to-item %1 payload-type bucket-name) payload)))
