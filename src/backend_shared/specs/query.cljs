(ns backend-shared.specs.query
  (:require [cljs.spec :as spec]))

(spec/def :es/query map?)
(spec/def :query/es (spec/keys :req-un [:es/query]))

(spec/def :query/backend (spec/or :identity   :query/identity
                                  :collection :query/collection
                                  :course     :query/course
                                  :resource   :query/resource
                                  :search     :query/es
                                  :error      :offcourse/error))
