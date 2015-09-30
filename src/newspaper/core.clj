(ns newspaper.core
  (:require [compojure.route :as route]
            [noir.session :as session]
				    [ring.util.response :as response])
  (:use [compojure.core :only [defroutes GET POST DELETE PUT]]
        [ring.adapter.jetty :only [run-jetty]]
        [newspaper.main :only [main-page do-delete-news put-news-in-session search-page search]]
        [newspaper.login :only [login-page do-login do-logout]]
        [newspaper.register :only [register-page do-register]]
        [db.db :only [insert-user insert-news get-user-by-username delete-news update-news insert-user-news]]
        [newspaper.news :only [news-page add-news update-page edit-news]]
        [ring.middleware.reload :only [wrap-reload]]
        [ring.middleware.stacktrace :only [wrap-stacktrace]]
        [ring.middleware.params :only [wrap-params]]))

(defroutes handler
  (GET "/" [] (main-page))
  (POST "/" [news] (put-news-in-session news))
  (POST "/search" [news-type] (search-page news-type))
  (POST "/updateNews1" [id news-name news-type news-description news-image] (update-page id news-name news-type news-description news-image))
  (POST "/updateNews" [id news-name news-type news-description] (edit-news id news-name news-type news-description))
  (DELETE "/" [id] (do-delete-news (Integer/valueOf id)))
  (GET "/login" [] (let [user (session/get :user)] (if-not user (login-page) (main-page))))
  (POST "/login" [username password] (do-login username password))
  (GET "/logout" [] (do (do-logout) (response/redirect "/")))
  (GET "/register" [] (let [user (session/get :user)] (if-not user (register-page) (main-page))))
  (POST "/register" [first-name last-name email username password confirm-password] (do-register first-name last-name email username password confirm-password)) 
  (GET "/news" [] (let [user (session/get :user)] (if user (news-page) (login-page))))
  (POST "/news" [news-name news-type news-description] (add-news news-name news-type news-description))
  (route/resources "/")
  (route/not-found "Page not found."))

 (def app
  (-> #'handler
    (wrap-reload)
    (wrap-params)
    (session/wrap-noir-flash)
    (session/wrap-noir-session)
    (wrap-stacktrace)))


 (defn start-jetty-server []
   (run-jetty #'app {:port 63010 :join? false})
   (println "\nWelcome to the Newspaper. Browse to http://localhost:63010 to get started!"))
 
 (defn insert-test-user [] 
  (insert-user "Test user" "test@test.com" "test" "test"))
 

 (defn insert-test-data [] 
   (let [user (get-user-by-username "test")]
     (do
       (insert-user-news "Formula 1" "Sport" "Renault have been in talks to buy Lotus for several months and the latter's financial position has become precarious without a deal in place. But in a statement on Monday, Renault said it was the first step towards the project of a Renault Formula 1 team. Lotus F1 were due in court on Monday over unpaid tax, but the case was adjourned until 7 December." "images/img3.jpg" "2015-09-27" (:_id user))
       (insert-user-news "Giant pandas" "Fun" "They are cute, for sure. But don't go in for a cuddle; pandas can deliver one heck of a bite. Eating bamboo is a blinding evolutionary strategy. They do not deserve to go extinct." "images/img1.jpg" "2015-09-28" (:_id user))
       (insert-user-news "Body's 'chemical calendar'" "Health" "The team, reporting in Current Biology, found a cluster of thousands of cells that could exist in either a summer or winter state.
                                                           They use the lengthening day to switch more of them into summer mode and the opposite when the nights draw in.
                                                           The annual clock controls when animals breed and hibernate and in humans may be altering the immune system.
                                                           A team from the Universities of Manchester and Edinburgh analysed the brains of sheep at different times of the year." "images/img2.jpg" "2015-09-27" (:_id user))
       
       )))
 (defn -main [& args]
   (do
     (start-jetty-server)
      (let [user (get-user-by-username "test")]
       (if-not user (do 
                 (insert-test-user) 
                 (insert-test-data))))))


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
