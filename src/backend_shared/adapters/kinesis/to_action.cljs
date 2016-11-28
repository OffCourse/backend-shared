(ns backend-shared.adapters.kinesis.to-action
  (:require [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.action.index :as action]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def deployment-stage (.. js/process -env -serverlessStage))

(defmulti to-action sp/resolve)

(defmethod to-action [:put :errors] [[action-type payload :as action]]
  (let [stream-name (str (name (second (sp/resolve action))) "-" deployment-stage)
        errors (map #(assoc (meta action) :data %1) action)]
    (log/log "invalid action" (clj->js errors))
    (action/create [action-type {:stream-name stream-name
                                 :records errors}])))

(defmethod to-action :default [[action-type payload :as action]]
  (let [stream-name (str (name (second (sp/resolve action))) "-" deployment-stage)]
    (action/create [action-type {:stream-name stream-name
                                 :records payload}])))

