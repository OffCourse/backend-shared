(ns backend-shared.service.perform
  (:require [shared.protocols.specced :as sp]))

(defmulti perform (fn [service action]
                    (sp/resolve action)))
