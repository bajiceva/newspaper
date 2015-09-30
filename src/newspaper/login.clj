(ns newspaper.login
  (:require [noir.session :as session]
            [ring.util.response :as response])
  (:use  [newspaper.template :only [get-template]]
         [hiccup.form :only [form-to label text-field password-field submit-button]]
         [db.db :only [get-user-by-username]]))

(defn login-page
  "Displays login page."
  []
  (get-template "Newspaper" 
    [:div.content
     [:p.logintitle "Please enter your username and password: "]
      [:p.loginerror (session/flash-get :login-error)]
      (form-to [:post "/login"]
              [:div.loginform
               [:div 
               (label {:class "loginlabel"} :username "Username: ")
               (text-field :username)]
               [:div
               (label {:class "loginlabel"} :password "Password: ")
               (password-field :password)]
               [:div
                (submit-button {:class "button"} "Log in")]])]))

(defn- check-user
  "Check if the user with given username and passwored is valid."
  [user password]
    (cond
      (nil? user) "User with given username does not exist."
      (not= password (:password user)) "Password is not correct."
      :else true))

(defn do-login
  "If username and password are correct, put the information into session that the user is logged in."
  [username password]
  (let [lu (clojure.string/lower-case username)
        user (get-user-by-username lu)
        error-msg (check-user user password)]
    (if-not (string? error-msg) 
      (do 
        (session/put! :user user)
        (response/redirect "/"))
      (do
        (session/flash-put! :login-error error-msg)
        (response/redirect "/login")))))

(defn do-logout []
  (session/remove! :user))

