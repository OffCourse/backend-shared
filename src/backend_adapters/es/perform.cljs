(ns backend-adapters.es.perform
  (:require [cljs.core.async :as async]
            [clojure.walk :as walk]
            [shared.protocols.loggable :as log]
            [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def AWS (node/require "aws-sdk"))
(def path (node/require "path"))
(def creds (AWS.EnvironmentCredentials. "AWS"))
(def HTTP (AWS.NodeHttpClient.))

(defn create-request [endpoint {:keys [id item index-name]}]
  (let [req (AWS.HttpRequest. endpoint)
        headers (aget req "headers")]
    (aset req "method" "POST")
    (aset req "path"  (.join path "/" "offcourse" (name index-name) id))
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
                       (let [js-item (.parse js/JSON @item)
                             {:keys [message] :as res} (cv/to-clj js-item)]
                         (if message
                           (async/put! c {:error message})
                           (async/put! c res))
                         (async/close! c))))))

(defn -save [req]
  (let [c (async/chan)]
    (.handleRequest HTTP req nil #(handle-response %1 c))
    c))

(defn perform [{:keys [endpoint] :as this} [_ payload]]
  (go
    (let [queries     (cv/to-search (into [] payload))
          requests    (map #(create-request endpoint %1) queries)
          query-chans (async/merge (map #(-save %) requests))
          merged-res  (async/<! (async/into [] query-chans))
          errors      (filter (fn [{:keys [error]}] error) merged-res)
          success     (remove (fn [{:keys [error]}] error) merged-res)]
      {:saved success
       :errors errors})))
