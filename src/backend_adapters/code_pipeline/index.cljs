(ns backend-adapters.code-pipeline.index
  (:require [cljs.nodejs :as node]
            [shared.protocols.actionable :refer [Actionable]]
            [shared.protocols.loggable :as log]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))

(defn create [_]
  (specify {:instance (AWS.CodePipeline.)}
    Actionable
    (-perform [{:keys [instance]} [_ {:keys [jobId]}]]
      (let [c (async/chan)
            params (clj->js {:jobId jobId})
            res    (.putJobSuccessResult instance params #(async/put! c {:error %1
                                                                         :success %2}))]
        c))))
