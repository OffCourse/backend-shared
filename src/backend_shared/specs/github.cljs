(ns backend-shared.specs.github
  (:require [cljs.spec :as spec]))

(spec/def ::owner string?)
(spec/def ::name string?)
(spec/def ::mode string?)
(spec/def ::content string?)
(spec/def ::type string?)
(spec/def ::size int?)
(spec/def ::sha string?)
(spec/def ::path string?)
(spec/def ::tree (spec/* map?))

(spec/def :github/repo   (spec/keys :req-un [::name ::owner ::sha ::tree
                                             :base/url :base/user-name]))

(spec/def :github/course (spec/keys :req-un [::path ::content ::sha :base/url :base/user-name]))
