(ns kunismos.pages
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css]]
            [hiccup.form :refer [form-to text-area submit-button text-field password-field]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect]]
            [kunismos.db :as db]))

;; The main wrapper for all kunismos pages

(defn page [body & args]
  (html5 
    {:lang "en"}
    [:head (include-css "css/kunismos.css")]
    [:body
     [:header [:h1 "Kunismos"]]
     [:main body args]]))

;; The pages

(defn landing [{{naughty :naughty} :params}]
  (page
    (when (boolean naughty)
      [:p "You must create an account or be logged in to do that"])
    [:h1 "Kunismos landing"]))

;TODO: Write the GET /new page
(defn create [_]
  (page 
    (form-to [:post ""]
             (anti-forgery-field)
             (text-area {:autofocus "autofocus"} "text")
             (submit-button "submit"))))

;TODO: Write the GET /journal page
(defn journal [{{{id :id} :identity} :session}]
  (page
    (html [:h1 "Your entries"]
          [:ul (for [entry (db/get-entries id)] [:li (:text entry)])])))

;TODO: Fix the POST /new page
(defn create-post [{{text :text} :params}]
  (db/create-entry text 1)
  (redirect "/" 303)) ;Code 303 indicates that we're redirecting to a GET resource after a POST

;TODO: Write the GET /login page
;TODO: Write the POST /login return page stuff in here usint kunismos.auth functions as utility functions, not worker functions
(defn login [{{err :error} :params}]
  (page
    (when (boolean err)
      [:p "Invalid username or password!"])
    (form-to [:post "/login"]
             (anti-forgery-field)
             (text-field "username")
             (password-field "password")
             (submit-button "submit"))))


;TODO: Write the GET /createaccount page
(defn createaccount [{{err :error} :params}]
  (page
    (when (boolean err)
      [:p "Invalid username or password"])
    (form-to [:post "/createaccount"]
             (anti-forgery-field)
             (text-field "username")
             (password-field "password")
             (submit-button "submit"))))

;TODO: Write the POST /createaccount page
;TODO: Write the GET /landing page
;TODO: Write the GET,POST /logout redirect-page
;TODO: Write the GET / redirect-page
