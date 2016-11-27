(ns backend-shared.service.perform
  (:require [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]))

(defmulti perform (fn [service action]
                    (let [{:keys [user guest]} (meta action)]
                      (if (or user guest)
                        (concat (sp/resolve action) (if user [:user] [:guest]))
                        (sp/resolve action)))))
