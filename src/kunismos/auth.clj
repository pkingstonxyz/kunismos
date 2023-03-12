(ns kunismos.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.hashers :as hashers]
            [ring.util.response :refer [redirect]]))

(def backend (backends/session))

(def users {"admin" {:username "admin"
                     :hashed-password (hashers/encrypt "adminpass")
                     :roles #{:user :admin}}
            "user"  {:username "user"
                     :hashed-password (hashers/encrypt "userpass")
                     :roles #{:user}}})

(defn lookup-user [username password]
  (when-let [user (get users username)]  ; Use a database IRL
    (when (hashers/verify password (get user :hashed-password))
      (dissoc user :hashed-password)))) ; Strip out user password

(defn do-login [{{username :username password :password nextpage :next} :params
                 session :session :as _}] ;Change _ to req
  (if-let [user (lookup-user username password)]    ; lookup-user defined elsewhere
    (assoc (redirect (or nextpage "/"))                 ; Redirect to "next" or /
           :session (assoc session :identity user)) ; Add an :identity to the session
    (str "Login page goes here")))                  ; If no user, show a login page


(defn do-logout [{session :session}]
  (-> (redirect "/login" 303)                           ; Redirect to login
      (assoc :session (dissoc session :identity)))) ; Remove :identity from session
