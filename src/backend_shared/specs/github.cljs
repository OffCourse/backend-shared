(ns backend-shared.specs.github
  (:require [cljs.spec :as spec]))

(spec/def ::owner string?)
(spec/def ::name string?)
(spec/def ::mode string?)
(spec/def ::type string?)
(spec/def ::size int?)

(spec/def ::sha string?)

(spec/def :github/repo   (spec/keys :req-un [::name ::owner ::sha]
                                    :opt-un [:base/user-name]))

(spec/def ::path   string?)
(spec/def :github/course (spec/keys :req-un [::path ::sha :base/url :base/user-name]))
