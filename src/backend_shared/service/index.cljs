(ns backend-shared.service.index
  (:require [backend-shared.aws-event.index :as aws-event]
            [backend-adapters.index :as adapters]
            [backend-shared.service.fetch :as fetch]
            [backend-shared.service.perform :as perform]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :refer [Queryable]]))

(def stage (.. js/process -env -serverlessStage))

(def table-names  {:identities         (.. js/process -env -identitiesTable)
                   :identity           (.. js/process -env -identitiesTable)
                   :courses            (.. js/process -env -coursesTable)
                   :resources          (.. js/process -env -resourcesTable)
                   :bookmarks          (.. js/process -env -bookmarksTable)
                   :profiles           (.. js/process -env -profilesTable)})

(def bucket-names {:github-courses     (.. js/process -env -githubCoursesBucket)
                   :portraits          (.. js/process -env -assetsBucket)
                   :raw-resources      (.. js/process -env -resourcesBucket)
                   :courses            (.. js/process -env -rawCoursesBucket)
                   :raw-users          (.. js/process -env -rawUsersBucket)
                   :github-repos       (.. js/process -env -githubReposBucket)})

(def stream-names {:raw-users          (.. js/process -env -rawUsersStream)
                   :bookmarks          (.. js/process -env -bookmarksStream)
                   :courses            (.. js/process -env -coursesStream)
                   :raw-resources      (.. js/process -env -rawResourcesStream)
                   :raw-portraits      (.. js/process -env -rawPortraitsStream)
                   :resources          (.. js/process -env -resourcesStream)
                   :identities         (.. js/process -env -identitiesStream)
                   :errors             (.. js/process -env -errorsStream)
                   :profiles           (.. js/process -env -profilesStream)
                   :raw-repos          (.. js/process -env -rawReposStream)
                   :github-repos       (.. js/process -env -githubReposStream)
                   :raw-github-courses (.. js/process -env -rawGithubCoursesStream)
                   :github-courses     (.. js/process -env -githubCoursesStream)})

(def config {:table-names table-names
             :bucket-names bucket-names
             :stream-names stream-names})

(defrecord Service []
  Actionable
  (-perform [service payload] (perform/perform service payload))
  Queryable
  (-fetch [service query] (fetch/fetch service query)))

(defn initialize-adapters [adapter-names env]
  (reduce (fn [acc val] (assoc acc val ((val adapters/constructors) env)))
          {} adapter-names))

(defn log-incoming [event context]
  (log/log "")
  (log/log "---------------")
  (log/log "INCOMING EVENT: " event)
  (log/log "---------------")
  (log/log "")
  (log/log "---------------")
  (log/log "FUNCTION CONTEXT: " context)
  (log/log "---------------")
  (log/log ""))

(defn initialize [{:keys [specs mappings callback event context environment adapters] :as config}]
  (specs)
  (mappings)
  (log-incoming event context)
  (map->Service (merge {:stage stage
                        :callback callback
                        :context (cv/to-clj context)
                        :event   (aws-event/create event)}
                        (initialize-adapters adapters environment))))

(defn res [code body] {:statusCode code
                           :headers {:Access-Control-Allow-Origin "*"}
                           :body body})

(defn accepted
  ([{:keys [callback]}] (callback nil (clj->js (res 202 nil))))
  ([{:keys [callback]} payload] (callback nil (clj->js (res 202 payload)))))

(defn unauthorized [{:keys [callback]} error] (callback (str "[401] " error)))

(defn done [{:keys [callback] :as s} payload] (callback nil (clj->js payload)))

(defn fail [{:keys [callback]} error] (callback (clj->js error)) nil)

(def fetch fetch/fetch)
(def perform perform/perform)
