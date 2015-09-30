(ns newspaper.register
  (:require [noir.session :as session]
            [ring.util.response :as response])
  (:use [newspaper.template :only [get-template]]
        [hiccup.form :only [form-to label text-field password-field submit-button]]
        [db.db :only [insert-user get-user-by-email get-user-by-username]]))

(defn register-page
  "Displays register page."
  []
  (get-template "Newspaper"
  [:div.content
   [:p.registertitle "Please complete the registration information:" ]
    [:p.registererror (session/flash-get :register-error)]
   (form-to [:post "/register"]
            [:div.registerform
             [:div
              (label {:class "registerlabel"} :first-name "First Name: ")
              (text-field :first-name (session/flash-get :first-name))]
             [:div 
              (label {:class "registerlabel"} :last-name "Last Name: ")
              (text-field :last-name (session/flash-get :last-name)) ]
             [:div
              (label {:class "registerlabel"} :email "Email: ")
              (text-field :email (session/flash-get :email))]
             [:div
              (label {:class "registerlabel"} :username "Username: ")
              (text-field :username (session/flash-get :username))]
             [:div
              (label {:class "registerlabel"} :password "Password: ")
              (password-field :password)]
             [:div
              (label {:class "registerlabel"} :confirm-password "Confirm password: ")
              (password-field :confirm-password)]
             [:div
              (submit-button {:class "button"} "Register")]])]))

(defn- check-user-data
  "Check enetered user data." 
  [first-name last-name email username password confirm-password]
  (cond
    (> 1 (.length first-name)) "First name must be at least 1 character long."
    (> 1 (.length last-name)) "Last name must be at least 1 character long."
    (< 35 (.length (str last-name first-name))) "First and last should not be longer than 35 characters combined."
    (not (nil? (get-user-by-email email))) "Email address exists. Please choose another one."
    (not (nil? (get-user-by-username username))) "Username exists. Please choose another one."
    (> 5 (.length username)) "Username must be at least 5 characters long."
    (< 15 (.length username)) "Username should not be longer than 15 characters."
    (> 4 (.length password)) "Password must be at least 4 characters long."
    (not= password confirm-password) "Password and confirmed password are not the same."
    :else true))

(defn do-register 
  "If user data is entered properly, add user to database."
  [first-name last-name email username password confirm-password]
  (let [lu (clojure.string/lower-case username)
        register-error-msg (check-user-data first-name last-name email lu password confirm-password)]
    (if-not (string? register-error-msg)
      (do
        (insert-user (str first-name " " last-name) email lu password)
        (response/redirect "/"))
      (do
        (session/flash-put! :register-error register-error-msg)
        (session/flash-put! :first-name first-name)
        (session/flash-put! :last-name last-name)
        (session/flash-put! :email email)
        (session/flash-put! :username lu)
        (response/redirect "/register")))))




