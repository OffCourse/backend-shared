(ns backend-adapters.s3.to-item
  (:require [clojure.string :as str]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]))

(defn create-item [item-key bucket-name item-data]
  {:Bucket bucket-name
   :Key item-key
   :Body item-data})

(defmulti to-item (fn [payload bucket-name]
                    (sp/resolve (payload/create payload))))

(defmethod to-item :github-repo [{:keys [name sha user-name] :as repo} bucket-name]
  (create-item (str user-name "/" sha) bucket-name (cv/to-json repo)))

(defmethod to-item :portrait [{:keys [user-name portrait-data]} bucket-name]
  (let [key (str "portraits/" user-name ".jpg")]
    (create-item key bucket-name portrait-data)))

(defmethod to-item :raw-resource [{:keys [url] :as resource} bucket-name]
  (let [key (str (-> url (str/replace #"[:&@/,<>`']" "-") (str "/embedly")))]
    (create-item key bucket-name (cv/to-json resource))))

(defmethod to-item :course [course bucket-name]
  (create-item (str (.now js/Date)) bucket-name (cv/to-json course)))

(defmethod to-item :raw-user [{:keys [user-name] :as user} bucket-name]
  (create-item user-name bucket-name (cv/to-json user)))

(defmethod to-item :github-course [{:keys [path sha user-name] :as course} bucket-name]
  (create-item (str user-name "/" sha) bucket-name (cv/to-json course)))
