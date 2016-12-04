(ns backend-shared.specs.index
  (:require [cljs.spec :as spec]
            [shared.specs.index]
            [backend-shared.specs.raw]
            [backend-shared.specs.query]
            [backend-shared.specs.payload]
            [backend-shared.specs.aws.index]
            [backend-shared.specs.github]
            [backend-shared.specs.embedly]))

(spec/def :offcourse/action  :action/valid)
(spec/def :offcourse/query   :query/backend)
#_(spec/def :offcourse/payload :payload/backend)


