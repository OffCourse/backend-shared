(ns backend-shared.specs.index
  (:require [cljs.spec :as spec]
            [shared.specs.index]
            [backend-shared.specs.query]
            [backend-shared.specs.aws.index]
            [backend-shared.specs.github]
            [backend-shared.specs.embedly]))

(spec/def ::checkpoint  (spec/keys :req-un [:checkpoint/task :resource/resource-url]))
(spec/def ::checkpoints (spec/coll-of ::checkpoint))
(spec/def ::course      (spec/keys :req-un [:course/curator :course/goal ::checkpoints]))
(spec/def ::user        (spec/keys :req-un [:base/user-name :auth/auth-profile]))

(spec/def :offcourse/action :action/valid)
(spec/def :offcourse/query  :query/backend)
(spec/def :raw/course ::course)
(spec/def :raw/user ::user)
