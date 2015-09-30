(ns newspaper.news
  (:require [noir.session :as session]
            [ring.util.response :as response])
  (:use [newspaper.template :only [get-template]]
        [hiccup.form :only [form-to label text-field text-area submit-button hidden-field drop-down]]
        [db.db :only [insert-news update-news]]))

(defn check-news-data
  "Checks news data."
  [news-name news-type news-description]
  (cond
    (> 1 (.length news-name)) "Name is required."
    (> 1 (.length news-description)) "Text is required."
    :else true)
  )

(defn add-news
  "Add news."
  [news-name news-type news-description]
  (let [news-error-message (check-news-data news-name news-type news-description)]
    (if (= 0 (compare "Fun" news-type))
      (do
        (def news-image "images/img1.jpg"))
    )
    (if (= 0 (compare "Health" news-type))
      (do
        (def news-image "images/img2.jpg"))
    )
    (if (= 0 (compare "Sport" news-type))
      (do
        (def news-image "images/img3.jpg"))
    )
    (if-not (string? news-error-message)
      (do
        (insert-news news-name news-type news-description news-image)
        (response/redirect "/"))
      (do
        (session/flash-put! :news-error news-error-message)
        (session/flash-put! :news-name news-name)
        (session/flash-put! :news-type news-type)
        (session/flash-put! :news-description news-description)
     (session/flash-put! :news-image news-image)
        (response/redirect "/news")))))               

(defn news-page
  "Show news page"
  []
  (get-template "Newspaper"
   [:div.content
    [:p.newstitle "Write an article:"]
     [:p.newserror (session/flash-get :news-error)]
    (form-to [:post "/news"]
             [:div.newnewsform
              [:div
              (label {:class "newslabel"} :news-name "Subject:")
               (text-field :news-name (session/flash-get :news-name))]
              [:div
               (label {:class "newslabel"} :news-type "Area:")
                (drop-down {:class "dropdown1"} :news-type  ["Fun","Sport","Health"])]
              [:div
               (label {:class "newslabel desc"} :news-description "Text:")
                (text-area {:class "textarea"} :news-description (session/flash-get :news-description))]
               [:div
                (submit-button {:class "button"} "Save")]])]))
                
(defn update-page [id news-name news-type news-description news-image]
  (get-template "Newspaper"
   [:div.content
    [:p.newstitle "Edit article:"]
     [:p.newserror (session/flash-get :news-error)]
    (form-to [:post "/updateNews"]
             [:div.newnewsform
              [:div
              (label {:class "newslabel"} :news-name "Subject:")
               (text-field :news-name news-name)]
              [:div
               (label {:class "newslabel"} :news-type "Area:")
               (drop-down {:class "dropdown1"} :news-type  [news-type,"Fun","Sport","Health"])]
              [:div
               (label {:class "newslabel desc"} :news-description "Text:")
                (text-area {:class "textarea"} :news-description news-description)]
               [:div
                (hidden-field :id id)
                (submit-button {:class "button"} "Update")]])]))
  
(defn edit-news
  "Updates news from database"
  [id news-name news-type news-description]
  (let [news-error-message (check-news-data news-name news-type news-description)]
  (if (= 0 (compare "Fun" news-type))
      (do
        (def news-image "images/img1.jpg"))
    )
    (if (= 0 (compare "Health" news-type))
      (do
        (def news-image "images/img2.jpg"))
    )
    (if (= 0 (compare "Sport" news-type))
      (do
        (def news-image "images/img3.jpg"))
    )
  (if-not (string? news-error-message)
   (do
     (update-news id news-name news-type news-description news-image)
     (response/redirect "/"))  
   (do
        (session/flash-put! :news-error news-error-message)
        (session/flash-put! :news-name news-name)
        (session/flash-put! :news-type news-type)
        (session/flash-put! :news-description news-description)
        (session/flash-put! :news-image news-image)
        (response/redirect "/news")))))    
