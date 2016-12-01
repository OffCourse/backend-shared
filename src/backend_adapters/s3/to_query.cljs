(ns backend-adapters.s3.to-query)

(defn to-query [{:keys [bucket-name item-key]}]
  {:Bucket bucket-name
   :Key item-key})
