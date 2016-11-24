(ns backend-shared.specs.embedly
  (:require [cljs.spec :as spec]))

(spec/def ::description (spec/nilable string?))

(spec/def ::url string?)
(spec/def ::original_url string?)
(spec/def ::provider_display string?)
(spec/def ::provider_name string?)

(spec/def ::title (spec/nilable string?))
(spec/def ::content (spec/nilable string?))
(spec/def ::type string?)

(spec/def ::name string?)

(spec/def ::score int?)
(spec/def ::count int?)

(spec/def ::author   (spec/keys :req-un [::name]))
(spec/def ::authors  (spec/* ::author))

(spec/def ::keyword  (spec/keys :req-un [::score ::name]))
(spec/def ::keywords (spec/* ::keyword))


(spec/def ::entity   (spec/keys :req-un [::count ::name]))

(spec/def ::entities (spec/* ::entity))

(spec/def :embedly/resource (spec/keys :req-un [::description ::url ::original_url
                                                ::authors ::keywords ::entities
                                                ::content ::title]))
