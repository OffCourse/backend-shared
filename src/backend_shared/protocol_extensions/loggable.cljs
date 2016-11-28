(ns backend-shared.protocol-extensions.loggable
  (:require [shared.protocols.loggable :as log :refer [Loggable]]))

(extend-protocol Loggable
  string
  (-log
    ([str] (.log js/console str))
    ([str args] (.log js/console str (.stringify js/JSON args))))
  (-error
    ([this] (.error js/console this)))
  array
  (-log
    ([obj] (.log js/console (.stringify js/JSON obj))))
  object
  (-log
    ([obj] (.log js/console (.stringify js/JSON obj)))
    ([obj args] (.log js/console
                      (.stringify js/JSON obj)
                      (.stringify js/JSON args))))
  (-error
    ([this] (.error js/console (.stringify js/JSON this)))))

