(ns backend-shared.protocol_extensions.convertible
  (:require [shared.protocols.convertible :as cv :refer [Convertible]]))

(extend-protocol Convertible
  nil
  (-to-clj [_]                  nil)
  array
  (-to-clj [js-arr]             (js->clj js-arr :keywordize-keys true))
  object
  (-to-clj [js-obj]             (js->clj js-obj :keywordize-keys true))
  string
  (-to-clj [string]             (->> string (.parse js/JSON) cv/to-clj))
  PersistentHashMap
  (-to-json     [obj]           (->> obj clj->js (.stringify js/JSON)))
  PersistentArrayMap
  (-to-json     [obj]           (->> obj clj->js (.stringify js/JSON))))
