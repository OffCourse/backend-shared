(ns backend-shared.specs.query
  (:require [cljs.spec :as spec]))

(spec/def :query/backend (spec/or :identity   :query/identity
                                  :collection :query/collection
                                  :error      :offcourse/error))
