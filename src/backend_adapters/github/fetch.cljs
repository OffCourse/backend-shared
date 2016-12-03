(ns backend-adapters.github.fetch
  (:require [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [clojure.walk :as walk]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [clojure.set :as set])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn yaml-file? [{:keys [path] :as ref}]
  (re-find #"\.yaml$" path))

(defn to-js [obj] (.parse js/JSON obj))

(def atob (node/require "atob"))
(def yaml (node/require "js-yaml"))
(def request (node/require "request"))

(defn authorized-header [api-key]
  {:user-agent    "offcourse"
   :authorization (str "token " api-key)})

(def unauthorized-header {:user-agent    "offcourse"})

(defn tree-url [endpoint repository]
  (let [{:keys [owner name sha]} repository]
    (str endpoint "/repos/" owner "/" name "/git/trees/" sha)))


(defn handle-response [c res body error]
  (let [statusCode (aget res "statusCode")
        response (-> body to-js cv/to-clj)]
    (if (< statusCode 400)
      (async/put! c response)
      (async/put! c {:error response}))
  (async/close! c)))

(defn handle-content [res]
  (->> res
       to-js
       cv/to-clj
       :content
       atob
       (.safeLoad yaml)
       js->clj
       walk/keywordize-keys))

(defn -fetch [url api-key]
  (let [c (async/chan)]
    (request (clj->js {:url url
                       :headers (if api-key (authorized-header api-key) unauthorized-header)})
             #(handle-response c %2 %3 %1))
    c))

(defmulti fetch (fn [_ query] (sp/resolve query)))

(defmethod fetch :raw-github-courses [{:keys [api-key endpoint] :as this} query]
  (go
    (let [urls        (map :url query)
          query-chans (async/merge (map #(-fetch % api-key) urls))
          merged-res  (async/<! (async/into [] query-chans))
          res         (set/join merged-res query {:sha :sha})]
      {:found res})))

(defmethod fetch :raw-repos [{:keys [api-key endpoint] :as this} query]
  (go
    (let [urls        (map #(tree-url endpoint %) query)
          query-chans (async/merge (map #(-fetch % api-key) urls))
          merged-res  (async/<! (async/into [] query-chans))
          errors      (filter (fn [{:keys [error]}] error) merged-res)
          res         (set/join merged-res query {:sha :sha})]
      {:found res
       :errors errors})))
