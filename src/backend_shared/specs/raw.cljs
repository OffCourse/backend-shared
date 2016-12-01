(ns backend-shared.specs.raw
  (:require [cljs.spec :as spec]))

(spec/def ::checkpoint  (spec/keys :req-un [:checkpoint/task :resource/resource-url]))
(spec/def ::checkpoints (spec/coll-of ::checkpoint))
(spec/def ::course      (spec/keys :req-un [:course/curator :course/goal ::checkpoints]))
(spec/def ::user        (spec/keys :req-un [:base/user-name :auth/auth-profile]))

(spec/def :raw/course ::course)

(spec/def :raw/repo   (spec/keys :req-un [::name ::owner ::sha]
                                 :opt-un [:base/user-name]))

(spec/def :raw/user ::user)

(spec/def :raw/github-course (spec/keys :req-un [::path ::sha :base/url :base/user-name]))
