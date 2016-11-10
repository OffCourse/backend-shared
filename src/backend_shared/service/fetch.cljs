(ns backend-shared.service.fetch
  (:require [shared.protocols.queryable :as qa]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]))

(defmulti fetch (fn [_ query] (sp/resolve query)))
