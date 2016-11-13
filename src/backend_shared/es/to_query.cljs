(ns backend-shared.es.to-query
  (:require [shared.protocols.specced :as sp]
            [cuerdas.core :as str]))

(defn query [subquery]
  {:query {:bool subquery}})

(defmulti to-query sp/resolve)

(defmethod to-query :course [{:keys [course-slug curator]}]
  (query {:must [{:match {:goal (str/humanize course-slug)}}
                 {:match {:curator curator}}]}))

(defmethod to-query :collection [{:keys [collection-type collection-name]}]
  (if (= collection-name "all")
    (query {})
    (let [query-key (case (keyword collection-type)
                      :flags :flags
                      :tags :checkpoints.tags
                      :curators :curator)]
      (query {:should [{:match {query-key collection-name}}]}))))
