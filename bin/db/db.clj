(ns db.db
  (:require [noir.session :as session]
            [clj-time.format :as time-format]
            [clj-time.core :as time])
  (:use [somnium.congomongo]))

(def conn 
  (make-connection "newspaper_db"))

(set-connection! conn)

(defn- generate-id [collection]
  "Generate entity identifier." 
  (:seq (fetch-and-modify :sequences {:_id collection} {:$inc {:seq 1}}
                          :return-new? true :upsert? true)))

(defn- insert-entity [collection values]
   "Insert an entity into database."
  (insert! collection (assoc values :_id (generate-id collection))))

(defn insert-user
  [name email username password]
  "Insert user into database." 
  (insert-entity :users 
                  {:name name
                   :email email
                   :username username
                   :password password}))

(defn get-user-by-username [username]
  "Find user by username."  
  (fetch-one :users :where {:username username}))

(defn get-user-by-email [email]
  "Find user by email."  
  (fetch-one :users :where {:email email}))

(defn insert-user-news
  [news-name news-type news-description news-image date-added user-id]
  "Insert news into database." 
  (insert-entity :news 
                  {:news-name news-name
                   :news-type news-type
                   :news-description news-description
                   :news-image news-image
                   :date-added date-added
                   :user-id user-id}))

(def parser-formatter (time-format/formatter "dd.MM.yyyy HH:mm"))

(defn insert-news
  "Inserts data for news into data base."
  [news-name news-type news-description news-image]
  (let [user (session/get :user)]
    (insert-entity :news
                   {:news-name news-name
                   :news-type news-type
                   :news-description news-description
                   :news-image news-image
                   :date-added (time-format/unparse parser-formatter (time/now))
                   :user-id (:_id user)})))
(defn get-all-news []
  "Return all news from the database." 
  (fetch :news :sort {:news-type -1}))

(defn get-latest-news []
  "Find the latest three news." 
  (fetch :news :sort {:date-added -1} :limit 3))


(defn update-news [id news-name news-type news-description news-image]
  "Update news from database."
   (fetch-and-modify :news {:_id (Integer/valueOf id)} 
                  {:news-name news-name
                   :news-type news-type
                   :news-description news-description
                   :news-image news-image
                   :date-added (time-format/unparse parser-formatter (time/now))
                   
                   }))

(defn delete-news [id]
  "Delete news from the database."
  (destroy! :news {:_id id}))

(defn get-news-by-name
  "Gets news by name"
  [news-name]
  (fetch-one :news :where {:news-name news-name}))

(defn get-news-by-type
  "Gets news by type"
  [news-type]
  (fetch :news :where {:news-type news-type}))

(defn get-news-types
  "Gets all types of news"
  []
  (fetch :news :only {:_id nil :news-name nil :news-description nil :news-image nil}))

