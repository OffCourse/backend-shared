(ns backend-shared.specs.aws.code-pipeline
  (:require [cljs.spec :as spec]))

(spec/def ::id string?)
(spec/def ::accountId string?)
(spec/def ::data map?)
(spec/def ::CodePipeline.job (spec/keys :req-un [::id ::accountId ::data]))

(spec/def ::event (spec/keys :req-un [::CodePipeline.job]))
