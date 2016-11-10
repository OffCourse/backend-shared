(ns backend-shared.service.perform
  (:require [shared.protocols.specced :as sp]
            [shared.specs.core :as specs]
            [shared.protocols.loggable :as log]
            [shared.protocols.actionable :as ac]))

(defmulti perform (fn [service action]
                    (sp/resolve action)))
