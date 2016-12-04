(ns backend-adapters.s3.to-item
  (:require [clojure.string :as str]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]))

(defn create-item [item-key bucket-name item-data]
  {:Bucket bucket-name
   :Key item-key
   :Body item-data})

(defmulti to-item (fn [payload payload-type bucket-name] payload-type))

(defmethod to-item :github-repos [{:keys [name sha user-name] :as repo} _ bucket-name]
  (create-item (str user-name "/" sha) bucket-name (cv/to-json repo)))

(defmethod to-item :portraits [{:keys [user-name portrait-data]} _ bucket-name]
  (let [key (str "portraits/" user-name ".jpg")]
    (create-item key bucket-name portrait-data)))

(defmethod to-item :raw-resources [{:keys [url] :as resource} _ bucket-name]
  (let [key (str (-> url (str/replace #"[:&@/,<>`']" "-") (str "/embedly")))]
    (create-item key bucket-name (cv/to-json resource))))

(defmethod to-item :courses [course _ bucket-name]
  (create-item (str (.now js/Date)) bucket-name (cv/to-json course)))

(defmethod to-item :raw-users [{:keys [user-name] :as user} _ bucket-name]
  (create-item user-name bucket-name (cv/to-json user)))

(defmethod to-item :github-courses [{:keys [path sha user-name] :as course} _ bucket-name]
  (create-item (str user-name "/" sha) bucket-name (cv/to-json course)))
