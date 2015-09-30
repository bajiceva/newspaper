(ns newspaper.template
  (:require [noir.session :as session])
  (:use  [hiccup.core :only [html]]
         [hiccup.page :only [include-css doctype]]))

(defn not-logged-in-menu
  "Generates the menu for all pages when the user is not logged in."
  []
  [:ul#menu
   [:li#home
    [:a {:href "/"} "Home"]]
   [:div#logreg
    [:li
    [:a {:href "/login"} "Login"]]
   [:li 
    [:a {:href "/register"} "Register"]]]])

(defn logged-in-menu
  "Generates the menu for all pages when the user is logged in."
  []
  [:ul#menu 
   [:li#home
    [:a {:href "/"} "Home"]]
   [:div#logreg
   [:li 
    [:a {:href "/news"} "New"]]
   [:li
    [:a {:href "/logout"} "Logout"]]]])

(defn get-template
  "Generates template for all pages in application."
  [title content]
  (html
    (doctype :xhtml-transitional)
    [:html {:xmlns "http://www.w3.org/1999/xhtml" "xml:lang" "en" :lang "en"} 
      [:head
        (include-css "/css/newspaper.css")
        [:meta {:charset "UTF-8"}]
        [:title title]]
      [:body
       (let [user (session/get :user)] 
         (if-not user (not-logged-in-menu) (logged-in-menu)))
         [:div#container
          [:div#titlelogo        
	          [:h1#title "Newspaper"]]
          content]
          [:div#footer ""]]]))
