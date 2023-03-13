(ns kunismos.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.hashers :as hashers]
            [ring.util.response :refer [redirect]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

;; All database stuff for creating and authorizing users

(def db {:dbtype "sqlite" :dbname "kunismos.sqlite"})
(def ds (jdbc/get-datasource db))

(defn gen-rand-char 
  "Returns a random uppercase or lowercase character"
  []
  (if (= 1 (rand-int 2))
    (char (+ 65 (rand-int 26)))
    (char (+ 97 (rand-int 26)))))

(defn gen-salt 
  "Return the salt for a password"
  []
  (reduce str (repeatedly 32 gen-rand-char)))

(defn salt-password
  "Returns a function that concats a string and salt"
  [salt]
  #(str % ":" salt))

(defn salt-user
  "Returns a user map with the salted password and salt as long as the map has a password in it"
  [user]
  (let [salt (gen-salt)]
    (-> user
        (assoc :salt salt)
        (update :password (salt-password salt)))))

(defn hash-user
  "Returns a user map with a salted-hashed password"
  [user]
  (update user :password hashers/encrypt))

(defn format-user
  "Returns a fully formated user map"
  [username password]
  (-> {:username username :password password}
      (salt-user)
      (hash-user)))

(defn verify-user
  "Verifies a password against a given user object. Returns a bool"
  [{user :userobj
    pass :password-attempt}]
  (:valid (hashers/verify ((salt-password (:salt user)) pass) (:password user))))

(defn create-insert-user-command-string
  "Creates the command-string used by the insert-user! method"
  [{username :username
    password :password
    salt     :salt}]
  (format "INSERT into users (username,password,salt) values (\"%s\",\"%s\",\"%s\")" username password salt)
  )

(defn insert-user!
  "Creates a new user in the `users` table"
  [user]
  (let [command  (create-insert-user-command-string user)]
    (jdbc/execute! ds [command])))

(defn get-user 
  "Returns a map of a user given the username from the users table"
  [username]
  (first (jdbc/execute! ds [(format "select * from users where username=\"%s\"" username)] {:return-keys true :builder-fn rs/as-unqualified-lower-maps})))

(defn is-user-in-db?
  "Checks if a username is already in the database"
  [username]
  (= (:username (get-user username)) username))

; TODO: Write a function that will allow users to change their password NOTE: This is a stretch goal

;; All auth handlers/routes/whatever these are called
;; NOTE: I credit a lot of this to Adam Bard's 2015 tutorial. I had to update a few of the things though

(def backend (backends/session))

(defn lookup-user [username password]
  (when-let [user (get-user username)]
    (when (verify-user {:userobj user :password-attempt password})
      (dissoc user :hashed-password)))) ; Strip out user password

(defn do-login [{{username :username password :password nextpage :next} :params
                 session :session :as _}] ;Change _ to req
  (if-let [user (lookup-user username password)]    ; lookup-user defined elsewhere
    (assoc (redirect (or nextpage "/") 303)                 ; Redirect to "next" or /
           :session (assoc session :identity user)) ; Add an :identity to the session
    (redirect "/login?error=true" 303)))                  ; If no user, show a login page

(defn do-logout [{session :session}]
  (-> (redirect "/login" 303)                           ; Redirect to login
      (assoc :session (dissoc session :identity)))) ; Remove :identity from session

(defn do-create-user 
  "Creates a user"
  [{{username :username password :password nextpage :next} :params session :session :as _}] ;Change _ to req
  (if-not (is-user-in-db? username)
    (do 
      (insert-user! (format-user username password))
      (redirect (or nextpage "/login") 303))
    (redirect "/createaccount?error=\"username-already-exists\"" 303)))

;; REPL Stuff

; Used to create/test users table in repl
;(jdbc/execute! ds ["CREATE TABLE users (id integer primary key autoincrement, username varchar(32) unique not null, password char(98) not null, salt varchar(32) not null, creation_date datetime default current_timestamp);"])
;(jdbc/execute! ds ["drop table users"])
;(jdbc/execute! ds ["SELECT name FROM sqlite_schema WHERE type ='table' AND name NOT LIKE 'sqlite_%';"]) ;list tables
;(jdbc/execute! ds ["select * from users"] {:return-keys true :builder-fn rs/as-unqualified-lower-maps})
;(let [users (jdbc/execute! ds ["select * from users"] {:return-keys true :builder-fn rs/as-unqualified-lower-maps})]
;  (map :id users))
;(insert-user! hashed-user)
