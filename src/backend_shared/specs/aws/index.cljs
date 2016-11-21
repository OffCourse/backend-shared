(ns backend-shared.specs.aws.index
  (:require [cljs.spec :as spec]))

(spec/def ::type string?)
(spec/def :aws/methodArn string?)
(spec/def :aws/authorizationToken string?)
(spec/def :aws/auth-event (spec/keys :req-un [::type :aws/methodArn :aws/authorizationToken]))
