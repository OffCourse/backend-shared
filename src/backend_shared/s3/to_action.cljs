(ns backend-shared.s3.to-action
  (:require [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]))

(def config (.config (node/require "dotenv")))
(def service-name (.. js/process -env -SERVERLESS_SERVICE_NAME))
(def deployment-stage (.. js/process -env -SERVERLESS_STAGE))

(defn to-item [key bucket-name data]
  {:item-key key
   :bucket-name (str service-name "-" bucket-name "-" deployment-stage)
   :item-data data})

(defn to-user-item [{:keys [user-name] :as user}]
  (to-item user-name "raw-users" (cv/to-json user)))

(defn to-course-item [course]
  (to-item (str (.now js/Date)) "courses" (cv/to-json course)))

(defn to-resource-item [{:keys [url] :as resource}]
  (let [key (str (-> url js/Buffer. (.toString "base64") (str "/embedly")))]
    (to-item key "resources" (cv/to-json resource))))

(defn to-portrait-item [{:keys [user-name portrait-data]}]
  (let [key (str "portraits/" user-name ".jpg")]
    (to-item key "assets" portrait-data)))

(defmulti to-action sp/resolve)

(defmethod to-action [:put :raw-users] [[_ users]]
  [:put (map to-user-item users)])

(defmethod to-action [:put :courses] [[_ courses]]
  [:put (map to-course-item courses)])

(defmethod to-action [:put :raw-portraits] [[_ portraits]]
  [:put (map to-portrait-item portraits)])

(defmethod to-action [:put :raw-resources] [[_ resources]]
  [:put (map to-resource-item resources)])
