(ns backend-shared.specs.index
  (:require [cljs.spec :as spec]
            [shared.specs.index]
            [backend-shared.specs.query]
            [backend-shared.specs.aws.index]))

(spec/def :offcourse/action :action/valid)
(spec/def :offcourse/query  :query/backend)
