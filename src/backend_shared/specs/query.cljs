(ns backend-shared.specs.query
  (:require [cljs.spec :as spec]))

(spec/def :es/query map?)
(spec/def :query/es (spec/keys :req-un [:es/query]))


(spec/def :query/backend (spec/or :raw-repos          (spec/coll-of :raw/repo)
                                  :raw-github-courses (spec/coll-of :raw/github-course)
                                  :bucket-items       (spec/coll-of :aws/bucket-item)
                                  :course             :query/course
                                  :resource           :query/resource
                                  :identity           :query/identity
                                  :collection         :query/collection
                                  :search             :query/es
                                  :raw-user           :raw/user
                                  :error              :offcourse/error))
