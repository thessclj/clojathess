(ns clojathess.prod
  (:require [clojathess.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
