(ns backend-shared.github.fetch
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

(defn to-js [obj]
  (.parse js/JSON obj))

(def config (.config (node/require "dotenv")))

(def api-key (.. js/process -env -GITHUB_API_KEY))

(def atob (node/require "atob"))
(def yaml (node/require "js-yaml"))
(def -request (node/require "request"))
(def request (.defaults -request
                        (clj->js {:headers {:user-agent    "offcourse"
                                            :Authorization (str "token " api-key)}})))

(defn tree-url [endpoint repository]
  (let [{:keys [owner name sha]} repository]
    (str endpoint "/repos/" owner "/" name "/git/trees/" sha)))

(defn handle-response [c res]
  (let [response (->> res
                      to-js
                      cv/to-clj)]
    (async/put! c response)
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

(defn -fetch [url]
  (let [c (async/chan)]
    (request (clj->js url) #(handle-response c %3))
    c))

(defmulti fetch (fn [_ query] (sp/resolve query)))

(defmethod fetch :github-courses [{:keys [endpoint] :as this} query]
  (go
    (let [urls        (map :url query)
          query-chans (async/merge (map #(-fetch %) urls))
          merged-res  (async/<! (async/into [] query-chans))
          res         (set/join merged-res query {:sha :sha})]
      {:found res})))

(defmethod fetch :github-repos [{:keys [endpoint] :as this} query]
  (go
    (let [urls        (map #(tree-url endpoint %) query)
          query-chans (async/merge (map #(-fetch %) urls))
          merged-res  (async/<! (async/into [] query-chans))
          res         (set/join merged-res query {:sha :sha})]
      {:found res})))
