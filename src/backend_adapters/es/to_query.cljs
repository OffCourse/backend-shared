(ns backend-adapters.es.to-query
  (:require [shared.protocols.specced :as sp]
            [cuerdas.core :as str]
            [shared.protocols.loggable :as log]))

(defn query [subquery]
  {:query {:bool subquery}})

(defmulti to-query sp/resolve)

(defmethod to-query :resource [{:keys [resource-url]}]
  {:index-name "resources"
   :query (query {:must [{:match {:resource-url resource-url}}]})})

(defmethod to-query :course [{:keys [course-slug curator]}]
  {:index-name "courses"
   :query (query {:must [{:match {:goal (str/humanize course-slug)}}
                         {:match {:curator curator}}]})})

(defmethod to-query :collection [{:keys [collection-type collection-name]}]
  (if (= collection-name "all")
    {:index-name "courses"
     :query (query {})}
    (let [query-key (case (keyword collection-type)
                      :flags :flags
                      :tags :checkpoints.tags
                      :curators :curator)]
    {:index-name "resources"
     :query (query {:should [{:match {query-key collection-name}}]})})))
