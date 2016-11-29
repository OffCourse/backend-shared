(ns backend-adapters.iam.index
  (:require [shared.protocols.actionable :refer [Actionable]]))

(defn template [principal-id effect methodArn]
  {:principalId principal-id
   :policyDocument {:Version "2012-10-17"
                    :Statement [{:Action "execute-api:Invoke"
                                 :Effect effect
                                 :Resource methodArn}]}})

(defn create-policy [{:keys [offcourse-id auth-id method-arn]}]
  (cond
    (and offcourse-id auth-id) (template (str "offcourse|" offcourse-id) "Allow" method-arn)
    auth-id (template  auth-id "Allow" method-arn)
    :default (template "hacker ;-)" "Deny" method-arn)))

(defn create [stage]
  (specify {:stage stage}
    Actionable
    (-perform [this [_ credentials]]
      {:policy (create-policy credentials)})))
