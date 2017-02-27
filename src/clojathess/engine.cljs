(ns clojathess.engine
  (:require [clojure.string :as str]))

(def nodes {:egnatia-dragoumi "you are at the Egnatia/Dragoumi cross."
            :port             "you are at the city port."
            :lambda-center    "you are at the lambda center."})

(defn describe-location [location nodes]
  (get nodes location))

(def edges {:egnatia-dragoumi {:port          [:south :road]
                               :lambda-center [:upstairs :elevator]}
            :port             {:egnatia-dragoumi [:north :road]}
            :lambda-center    {:egnatia-dragoumi [:downstairs :elevator]}})

(defn describe-path [[where what]]
  (str "there is a " (name what) " going " (name where) " from here."))

(defn describe-paths [location edges]
  (str/join
    " "
    (map
      (fn [[_ v]] (describe-path v))
      (get edges location))))

;; (describe-paths :living-room edges)

(def objects [:cpu :phone])
(def object-locations {:cpu :lambda-center
                       :phone :lambda-center})

(defn objects-at [location object-locations]
  (reduce-kv #(if (= location %3) (conj %1 %2) %1) [] object-locations))

;; (objects-at :living-room object-locations)

(defn describe-objects [loc obj-loc]
  (str/join " " (map #(str "you see a " (name %) " on the floor.")
                     (objects-at loc obj-loc))))

#_ (describe-objects :egnatia-dragoumi object-locations)

(def location (atom :lambda-center))

(defn look []
  (str/join " "
            [(describe-location @location nodes)
             (describe-paths @location edges)
             (describe-objects @location object-locations)]))

(defn move [direction]
  (let [new-loc (filter (fn [[go-loc [go-dir _]]]
                          (= direction go-dir)) (@location edges))]
    (if (seq new-loc)
      (reset! location (ffirst new-loc))
      :no-such-direction)))

(defn process [input]
  (let [[func param] (str/split input #" ")
        param (keyword param)]
    (condp = func
      "look" (look)
      "move" (move param)
      (str "Unknown command " func))))