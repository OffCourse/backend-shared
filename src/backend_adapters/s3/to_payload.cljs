(ns backend-adapters.s3.to-payload
  (:require [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [clojure.string :as str]))

(def config (.config (node/require "dotenv")))
(def service-name (.. js/process -env -SERVERLESS_SERVICE_NAME))
(def deployment-stage (.. js/process -env -SERVERLESS_STAGE))

(defn to-item [key bucket-name data]
  {:item-key key
   :bucket-name bucket-name
   :item-data data})

(defn to-user-item [{:keys [user-name] :as user} bucket-name]
  (to-item user-name bucket-name (cv/to-json user)))

(defn to-course-item [course]
  (to-item (str (.now js/Date)) "courses" (cv/to-json course)))

(defn to-gh-course-item [{:keys [path sha user-name] :as course}]
  (to-item (str user-name "/" sha) "github-courses" (cv/to-json course)))

(defmulti to-payload (fn [payload bucket-names]
                       (sp/resolve payload)))

(defn to-repo-item [{:keys [name sha user-name] :as repo} bucket-names]
  (to-item (str user-name "/" sha) bucket-names (cv/to-json repo)))

(defmethod to-payload [:put :github-repos] [[_ repos] bucket-names]
  [:put (map #(to-repo-item % bucket-names) repos)])

(defmethod to-payload [:put :github-courses] [[_ courses]]
  [:put (map to-gh-course-item courses)])

(defmethod to-payload [:put :courses] [[_ courses]]
  [:put (map to-course-item courses)])

(defmethod to-payload :raw-users [payload bucket-names]
  (let [payload-type (sp/resolve payload)
        bucket-name (payload-type bucket-names)]
    (log/log "B" (clj->js bucket-names))
    (map #(to-user-item %1 bucket-name) payload)))

(defn to-portrait-item [{:keys [user-name portrait-data]} bucket-names]
  (let [key (str "portraits/" user-name ".jpg")]
    (to-item key bucket-names portrait-data)))

(defmethod to-payload :portraits [portraits bucket-names]
  [:put (map #(to-portrait-item %1 bucket-names) portraits)])

(defn to-resource-item [{:keys [url] :as resource} bucket-names]
  (let [key (str (-> url (str/replace #"[:&@/,<>`']" "-") (str "/embedly")))]
    (to-item key bucket-names (cv/to-json resource))))

(defmethod to-payload :raw-resources [resources bucket-names]
  [:put (map #(to-resource-item %1 bucket-names) resources)])
