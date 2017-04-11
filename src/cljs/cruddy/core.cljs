(ns cruddy.core
  (:require [ajax.core :as ajax]
            [clojure.string :as str]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]
            [reagent-forms.core :refer [bind-fields]]
            [re-frame.core :as rf]))

;; -- Development --------------------------------------------------------------
(enable-console-print!)

;; -- Event Handlers -----------------------------------------------------------

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:items [{:description "foo"
             :workflows #{}}]}))

(rf/reg-event-db
 :change-description
 (fn [db [_ index value]]
   (assoc-in db [:items index :description] value)))

(rf/reg-event-db
 :select-workflow
 (fn [db [_ index workflow]]
   (update-in db [:items index :workflows] conj workflow)))

(rf/reg-event-db
 :deselect-workflow
 (fn [db [_ index workflow]]
   (update-in db [:items index :workflows] disj workflow)))

;; -- Query  -------------------------------------------------------------------

(rf/reg-sub
  :items
  (fn [db _]
    (:items db)))

;; -- View Functions -----------------------------------------------------------

(def all-workflows ["one" "two" "three" "four" "five" "six" "seven" "eight"
                    "nine" "ten" "eleven" "twelve" "thirteen" "fourteen"])
(defn item
  [index {:keys [description workflows]}]
  [:li
   {:key index}
   [:h4 "Description"]
   [:input {:type "text"
            :name "description"
            :value description
            :on-change #(rf/dispatch [:change-description index (-> % .-target .-value)])}]
   [:h4 "Workflows"]
   [:nav
    [:ul
     {:style {"height" "100px"
              "width" "18%"
              "overflow" "hidden"
              "overflowY" "scroll"}}
     (for [workflow all-workflows]
       (let [selected? (contains? workflows workflow)
             action (if selected?
                      :deselect-workflow
                      :select-workflow)]
         [:li {:key workflow
               :style {"cursor" "pointer"}
               :on-click #(rf/dispatch [action index workflow])}
          workflow (when selected? " (SELECTED)")]))]]])

(defn items
  []
  (let [items @(rf/subscribe [:items])]
    [:ul (map-indexed item items)]))

;; -- Entry Point -------------------------------------------------------------

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render [items] (js/document.getElementById "app")))
