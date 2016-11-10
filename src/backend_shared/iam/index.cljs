(ns backend-shared.iam.index
  (:require [shared.protocols.actionable :refer [Actionable]]))


(defn template [principal-id effect methodArn]
  {:principalId principal-id
   :policyDocument {:Version "2012-10-17"
                    :Statement [{:Action "execute-api:Invoke"
                                 :Effect effect
                                 :Resource methodArn}]}})

(defn create []
  (specify {}
    Actionable
    (-perform [this [_ {:keys [offcourse-id auth-id methodArn]}]]
      (cond
        (and offcourse-id auth-id) (template (str "offcourse|" offcourse-id) "Allow" methodArn)
        auth-id (template  auth-id "Allow" methodArn)
        :default (template "hacker ;-)" "Deny" methodArn)))))
