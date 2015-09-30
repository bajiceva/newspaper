(ns newspaper.main
  (:require [noir.session :as session]
            [ring.util.response :as response])
  (:use  [newspaper.template :only [get-template]]
         [hiccup.form :only [form-to hidden-field submit-button drop-down]]
         [db.db :only [get-all-news get-user-by-username delete-news get-news-by-name get-news-by-type get-news-types]]))

(defn logged-edit
  "Generates HTML for edit if user is logged."
  [news]
  [:div.buttons
  [:div.b1 
    (form-to [:post "/updateNews1"]
      [:div
       (hidden-field :id (news :_id))
       
       (hidden-field :news-name (:news-name news))
       (hidden-field :news-type (:news-type news))
       (hidden-field :news-description (:news-description news))
       (hidden-field :news-image (:news-image news))
       (submit-button {:class "button"} "Edit")
       ])]
  [:div 
    (form-to [:delete "/"]
      [:div
       (hidden-field :id (news :_id))
       (submit-button {:class "button"} "Delete")
       ])]])

(defn show-one-news
  "Generates HTML for one news."
  [news]
  [:div.newsinfo
   [:img {:src (:news-image news)}]
   [:h2 (:news-name news)]
   [:p.newstype (str ""(:news-type news))]
   [:p.newsdate (str ""(:date-added news))]
   [:p.desc (str ""(:news-description news))]
    (let [user (session/get :user)] 
     (if-not user () 
       (logged-edit news)))])

(defn show-all-news
  "Retrieves all news from database and displays them."
  []
  [:div.news
   (let [news (get-all-news)]
   (for [news news]
     (show-one-news news)))])

(defn do-delete-news 
  "Delete news."
  [id]
  (do
    (delete-news id)
    (session/flash-put! :message-info "Successfully deleted news.")
    (response/redirect "/")))

(defn put-news-in-session
  "Puts selected news in session"
  [news]
  (do
    (session/put! :news news)
    (response/redirect "/updateNews")))

(defn search
  "Search news"
  [news-type]
  [:div.news
  (let [news (get-news-by-type news-type)]
         (for [news news] 
            (show-one-news news)
          )
      )])

(defn main-page 
  "Displays main page."
  []
  (get-template "Newspaper" 
   [:div.content
    (form-to [:post "/search"]
             [:div.search
                   (drop-down {:class "dropdown"} :news-type  ["Fun","Sport","Health"])
                   (submit-button {:class "buttonSearch"} "Search" )])
    (show-all-news)
    ]))

(defn search-page 
  "Displays main page."
  [news-type]
  (get-template "Newspaper" 
   [:div.content
    (search news-type)
    ]))



