(ns backend-shared.specs.aws.index
  (:require [cljs.spec :as spec]
            [backend-shared.specs.aws.api :as api]
            [backend-shared.specs.aws.stream :as stream]
            [backend-shared.specs.aws.auth :as auth]))

(spec/def :aws/credentials ::auth/credentials)
(spec/def :aws/policy      ::auth/policy)
(spec/def :aws/record      ::stream/record)
(spec/def :aws/event       (spec/or :auth   ::auth/event
                                    :stream ::stream/event
                                    :api    ::api/event))
