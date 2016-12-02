(ns backend-shared.specs.payload
  (:require [cljs.spec :as spec]))

(spec/def :payload/backend (spec/or :bookmarks          (spec/coll-of :offcourse/bookmark)
                                    :courses            (spec/coll-of :offcourse/course)
                                    :raw-resources      (spec/coll-of :embedly/resource)
                                    :github-courses     (spec/coll-of :github/course)
                                    :github-repos       (spec/coll-of :github/repo)
                                    :raw-repos          (spec/coll-of :raw/repo)
                                    :raw-github-courses (spec/coll-of :raw/github-course)
                                    :raw-users          (spec/coll-of :raw/user)
                                    :resources          (spec/coll-of :offcourse/resource)
                                    :profiles           (spec/coll-of :offcourse/profile)
                                    :portraits          (spec/coll-of :offcourse/portrait)
                                    :identities         (spec/coll-of :offcourse/identity)
                                    :errors             (spec/coll-of :offcourse/error)
                                    :course             :offcourse/course
                                    :raw-resource       :embedly/resource
                                    :profile            :offcourse/profile
                                    :course             :offcourse/course
                                    :raw-user           :raw/user
                                    :github-repo        :github/repo
                                    :github-course      :github/course
                                    :portrait           :offcourse/portrait
                                    :nothing            nil?))
