(ns todoapp.core
    (:require-macros
              [cljs.core.async.macros :as asyncm :refer (go go-loop)])
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs.core.async :as async :refer (<! >! put! chan)]
              [taoensso.sente  :as sente :refer (cb-success?)]))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
       {:type :auto ; e/o #{:auto :ajax :ws}
       })]
  (def chsk          chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state   state)   ; Watchable, read-only atom
)

;; -------------------------
;; Views

(def classes ["btn btn-default" "btn btn-primary" "btn btn-success" "btn btn-info" "btn btn-warning" "btn btn-danger"])

(def quotes ["quote 1" "quote 2" "quote 2"])

(defn class-generator [] (rand-nth classes))

(defn quote-generator [] (rand-nth quotes))

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :maxLength 10
           :on-change #(reset! value (-> % .-target .-value))
           :on-key-press (fn [e] (if (= 13 (.-charCode e))
                                    (println (rand-nth classes))))}])

(defn home-page []
  (let [val (reagent/atom "foo")]
    (fn []
      [:div
       [:p "The value is now: " @val]
       [:h1 {:class (class-generator)} @val]
       [:h1 {:class (class-generator)} (quote-generator)]
       [:h1 {:class (class-generator)} @val]
       [:h1 {:class (class-generator)} (quote-generator)]
       [:h1 {:class (class-generator)} (quote-generator)]
       [:h2 {:class (class-generator)} (quote-generator)]
       [:h5 @val]
       [:p "Change it here: " [atom-input val]]])))


(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
