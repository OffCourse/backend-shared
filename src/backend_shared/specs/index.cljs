(ns backend-shared.specs.index
  (:require [cljs.spec :as spec]
            [shared.specs.index]
            [backend-shared.specs.raw]
            [backend-shared.specs.aws.index]
            [backend-shared.specs.github]
            [backend-shared.specs.embedly]))

(spec/def :offcourse/action  :action/valid)
