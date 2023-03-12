(ns kunismos.pages
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css]]
            [hiccup.form :refer [form-to text-area submit-button text-field password-field]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect]]
            [kunismos.db :as db]))

(defn page [body]
  (html5 
    {:lang "en"}
    [:head (include-css "css/kunismos.css")]
    [:body
     [:header [:h1 "Kunismos"]]
     [:main body]]))

(defn create [req]
  (page 
    (form-to [:post ""]
             (anti-forgery-field)
             (text-area {:autofocus "autofocus"} "text")
             (submit-button "submit"))))

(defn entries [req]
  (page
    (html [:h1 "Your entries"]
          [:ul (for [entry (db/get-entries)] [:li (:text entry)])])))

(defn add-post [text]
  (db/create-entry text)
  (redirect "/" 303)) ;Code 303 indicates that we're redirecting to a GET resource after a POST

(defn login [_]
  (page
    (form-to [:post "/login"]
             (anti-forgery-field)
             (text-field "username")
             (password-field "password")
             (submit-button "submit"))))
