(def project 'ofcourse/backend-shared)
(def version "0.2.0-SNAPSHOT")

(set-env!
 :resource-paths #{"src"}
 :source-paths #{"src"}
 :dependencies  '[[adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                  [adzerk/boot-cljs-repl       "0.3.3"          :scope "test"]
                  [adzerk/boot-reload          "0.4.12"         :scope "test"]
                  [pandeiro/boot-http          "0.7.3"          :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.0-SNAPSHOT" :scope "test"]
                  [boot-codox                  "0.9.6"          :scope "test"]
                  [com.cemerick/piggieback     "0.2.2-SNAPSHOT" :scope "test"]
                  [weasel                      "0.7.0"          :scope "test"]
                  [adzerk/bootlaces            "0.1.13"         :scope "test"]
                  [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
                  [funcool/cuerdas             "0.8.0"]
                  [com.rpl/specter             "0.12.0"]
                  [org.clojure/clojure         "1.9.0-alpha10"]
                  [offcourse/shared            "0.5.4"]
                  [org.clojure/core.async      "0.2.385"]
                  [org.clojure/test.check      "0.9.0"]
                  [org.clojure/clojurescript   "1.9.216"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.bootlaces      :refer :all]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[codox.boot :refer [codox]]
 '[pandeiro.boot-http    :refer [serve]])

(task-options! pom {:project     'offcourse/backend-shared
                    :version     version
                    :description "HELLO WORLD"})

(bootlaces! version)

(deftask build
  "Build and install the project locally."
  []
  (comp (build-jar)
        (codox :language :clojurescript
               :name "offcourse-backend-shared"
               :version version)
        (target)))



(deftask dev []
  (comp (watch)
        (cljs-repl)
        (build)))
