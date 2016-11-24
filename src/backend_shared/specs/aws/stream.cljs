(ns backend-shared.specs.aws.stream
  (:require [cljs.spec :as spec]))

(spec/def ::kinesis map?)
(spec/def ::s3 map?)
(spec/def ::dynamodb map?)

(spec/def ::record      (spec/or :kinesis (spec/keys :req-un [::kinesis])
                                 :dynamodb (spec/keys :req-un [::dynamodb])
                                 :s3 (spec/keys :req-un [::s3])))

(spec/def ::Records (spec/* ::record))

(spec/def ::event (spec/keys :req-un [::Records]))
