(ns backend-shared.specs.aws.api
  (:require [cljs.spec :as spec]))

(spec/def ::stage string?)
(spec/def ::body map?)

(spec/def ::event (spec/keys :req-un [::body ::stage]))
