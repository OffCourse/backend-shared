(ns backend-shared.kinesis.to-action
  (:require [cljs.nodejs :as node]
            [shared.protocols.specced :as sp]
            [clojure.walk :as walk]
            [shared.models.action.index :as action]
            [shared.protocols.loggable :as log]))

(def config (.config (node/require "dotenv")))
(def service-name (.. js/process -env -SERVERLESS_SERVICE_NAME))
(def deployment-stage (.. js/process -env -SERVERLESS_STAGE))

(defmulti to-action sp/resolve)

(defmethod to-action :default [[action-type payload :as action]]
  (let [stream-name (str (name (second (sp/resolve action))) "-" deployment-stage)]
    (action/create [action-type {:stream-name stream-name
                                 :records payload}])))

