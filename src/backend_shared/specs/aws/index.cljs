(ns backend-shared.specs.aws.index
  (:require [cljs.spec :as spec]
            [backend-shared.specs.aws.api :as api]
            [backend-shared.specs.aws.stream :as stream]
            [backend-shared.specs.aws.code-pipeline :as code-pipeline]
            [backend-shared.specs.aws.auth :as auth]))

(spec/def :aws/credentials ::auth/credentials)
(spec/def :aws/policy      ::auth/policy)
(spec/def :aws/record      ::stream/record)

(spec/def :aws/event       (spec/or :code-pipeline ::code-pipeline/event
                                    :auth          ::auth/event
                                    :stream        ::stream/event
                                    :api           ::api/event))

(spec/def ::input-queries (spec/coll-of :aws/bucket-item))
(spec/def ::output-queries (spec/coll-of :aws/bucket-item))
(spec/def :aws/build-artifacts (spec/keys :req-un [::input-queries ::output-queries]))

(spec/def ::jobId string?)
(spec/def :aws/code-pipeline-job (spec/keys :req-un [::jobId]))

(spec/def ::item-key    string?)
(spec/def ::bucket-name string?)
(spec/def :aws/bucket-item (spec/keys :req-un [::item-key ::bucket-name]))
