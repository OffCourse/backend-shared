(ns backend-shared.es.perform
  (:require [cljs.core.async :as async]
            [clojure.walk :as walk]
            [shared.protocols.loggable :as log]
            [cljs.nodejs :as node])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))
(def path (node/require "path"))
(def creds (AWS.EnvironmentCredentials. "AWS"))
(def HTTP (AWS.NodeHttpClient.))

(defn create-request [endpoint index-name [id item]]
  (let [req (AWS.HttpRequest. endpoint)
        headers (aget req "headers")]
    (aset req "method" "POST")
    (aset req "path"  (.join path "/" "offcourse" index-name id))
    (aset req "region" "us-east-1")
    (aset headers "presigned-expires" false)
    (aset headers "Host" (aget endpoint "host"))
    (aset req "body" (.stringify js/JSON (clj->js item)))
    (.addAuthorization (AWS.Signers.V4. req "es") creds (js/Date.))
    req))

(defn handle-response [resp c]
  (let [item (atom "")]
    (.on resp "data" #(swap! item str %1))
    (.on resp "end" #(do
                       (async/put! c @item)
                       (async/close! c)))))
(defn -save [req]
  (let [c (async/chan)]
    (.handleRequest HTTP req nil #(handle-response %1 c))
    c))

(defn perform [{:keys [endpoint] :as this} index-name [_ payload]]
  (go
    (let [queries (map #(create-request endpoint index-name %1) payload)
          query-chans (async/merge (map #(-save %) queries))
          res         (async/<! (async/into [] query-chans))]
        {:success res})))
