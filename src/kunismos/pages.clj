(ns kunismos.pages
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :refer [form-to text-area submit-button text-field password-field]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect]]
            [kunismos.db :as db]))

;; The main wrapper for all kunismos pages

(defn page [body & args]
  (html5 
    {:lang "en"}
    [:head 
     (include-css "css/kunismos.css")
     [:link {:rel "preconnect", :href "https://fonts.googleapis.com"}]
     [:link
      {:rel "preconnect",
       :href "https://fonts.gstatic.com", 
       :crossorigin ""}]
     [:link
      {:href
       "https://fonts.googleapis.com/css2?family=Fira+Code&display=swap",
       :rel "stylesheet"}]
     [:meta {:name "HandheldFriendly", :content "true"}]
     [:meta {:name "MobileOptimized", :content "320"}]
     [:meta
      {:name "viewport",
       :content
       "initial-scale=1.0, maximum-scale=1.0, width=device-width, user-scalable=no"}]]
    [:body
     ;[:header [:h1 "Kunismos"]]
     [:main body args]]))

;; Some common components

(def title 
  "The kunismos title h1"
  [:div 
   ;(include-js "js/landing.js")
   [:a {:href "/landing"} 
    ;[:h1#title.title {:onmouseover "Je(this)"} "κυνισμός"]]
    [:h1#title.title  "κυνισμός"]]
   (include-js "js/landing.js")
   ])

(defn errtext
  "The error text"
  [content]
  [:p {:style "color: red;"} content])

;; The pages

;(defn landing [{{naughty :naughty} :params}]
(defn landing [_]
  (page
    [:div.main_content
     title
     [:p.l_subtitle "kunismos - of or relating to Cynic philosophy or conduct"]
     [:p.l_tagline "simple journaling that gets out of your way"]
     [:nav.l_nav
      [:a {:href "/createaccount"} [:p "create account"]]
      [:a {:href "/login"} [:p "login"]]]]
    ))

;TODO: Write the GET /new page
(defn create [_]
  (page 
    (include-js "js/main.js")
    [:div.c_main
     [:nav.c_nav
      [:a {:href "/journal"} [:h2 "journal entries"]] 
      [:a {:href "/logout"} [:h2 "logout"]]] 
     [:form.c_form {:action "/create" :method "post"}
      (anti-forgery-field)
      [:textarea {:autofocus "autofocus" :placeholder "express yourself..." :name "text"}]
      [:input {:type "submit" :value "submit (ctrl+enter)"}]]]))

;TODO: Write the GET /journal page
(defn journal [{{{id :id} :identity} :session}]
  (page
    [:div.j_content
     title
     [:h2.j_heading "journal entries"]
     [:nav.j_nav
      [:a {:href "/"} [:h2 "journal"]]
      [:a {:href "/logout"} [:h2 "logout"]]]
     (for [entry (db/get-entries id)] 
            [:div.j_spacing 
             [:hr]
             [:p.j_date (:date entry)]
             [:pre.j_entry (:text entry)]])
     ]))

;TODO: Fix the POST /new page
(defn create-post [{{text :text} :params {{id :id} :identity} :session}]
  (db/create-entry text (int id))
  (redirect "/" 303)) ;Code 303 indicates that we're redirecting to a GET resource after a POST

;TODO: Write the GET /login page
;TODO: Write the POST /login return page stuff in here usint kunismos.auth functions as utility functions, not worker functions
(defn login [{{err :error} :params}]
  (page
    [:div.main_content
     title
    [:h2 "login"]
    (when (boolean err) (errtext "Invalid username or password"))
     (form-to [:post "/login"]
              (anti-forgery-field)
              (text-field "username")
              (password-field "password")
              (submit-button "login"))]))


;TODO: Write the GET /createaccount page
(defn createaccount [{{unameTaken :error
                       badUname   :invaliduname
                       badPassw   :invalidpass} :params}]
  (page
    ;(include-js "js/main.js")

    [:div.main_content
     title
    [:h2 "Create an account"]
    (when (boolean unameTaken) (errtext "Username has been taken"))
    (when (boolean badUname) (errtext "Username must be between 4 an 32 characters in length"))
    (when (boolean badPassw) (errtext "Password must be between 4 an 32 characters in length"))
     (form-to [:post "/createaccount"]
              (anti-forgery-field)
              (text-field "username")
              (password-field "password")
              (submit-button "create account"))]))

