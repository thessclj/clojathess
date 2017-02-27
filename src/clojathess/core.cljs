(ns clojathess.core
    (:require [reagent.core :as r]
              [clojathess.engine :as engine]))

;; -------------
;; State

(defonce app-state (r/atom {:text []}))

;; -------------------------
;; Views

(defn input-section []
  (let [input (r/atom "")]
    (fn []
      [:div
       [:input.u-full-width
        {:type        "text"
         :placeholder "Type a command"
         :value       @input
         :on-change   #(reset! input (-> % .-target .-value))
         :on-key-down #(case (.-which %)
                         13 (do
                              (swap! app-state update-in [:text] (fn [coll e] (take 5 (cons (engine/process e) coll))) @input)
                              (reset! input ""))
                         nil)}]])))

(defn output-section []
  [:div
   (let [text (:text @app-state)
         size (count text)]
     (for [i (-> size range)]
       ^{:key i} [:p.engine-text {:style {:opacity (/ (- size i) size)}} (nth text i)]))])

(defn main-wrapper []
  [:div.container.main-wrapper
   [:div.row
    [:h2 "Welcome to thess clj(s) adventure"]
    [:p.intro "Give a command using the input box below. For example 'look' or 'move downstairs'."]
    [input-section]
    [output-section]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [main-wrapper] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
