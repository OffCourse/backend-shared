(ns backend-shared.specs.aws.auth
  (:require [cljs.spec :as spec]))

(spec/def ::type string?)
(spec/def ::methodArn string?)
(spec/def ::authorizationToken string?)
(spec/def ::method-arn ::methodArn)
(spec/def ::auth-id (spec/nilable :auth/auth-id))
(spec/def ::principalId string?)
(spec/def ::policyDocument map?)

(spec/def ::event (spec/keys :req-un [::type ::methodArn ::authorizationToken]
                             :opt-un [:auth/auth-id]))

(spec/def ::credentials (spec/keys :req-un [::method-arn :auth/auth-token]
                                   :opt-un [::auth-id]))

(spec/def ::policy (spec/keys :req-un [::principalId ::policyDocument]))
