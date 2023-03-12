(ns kunismos.handler
  (:require [compojure.core :as c]
            [compojure.route :as route]
            ;[compojure.coercions :as coerce] ;Coercions
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            ;[ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.anti-forgery]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [kunismos.pages :as pages]
            [kunismos.auth :as auth]))

(c/defroutes app-routes
  ;(GET "/" [x :<< coerce/as-int, y :<< coerce/as-int] (str (+ x y))) ;This is an example of using query params and using coercion
  (c/GET "/" [] pages/create)
  (c/POST "/" [text] (pages/add-post text)) ;I'm doing it like this (function call instead of function) so I don't have to destructures the request in the kunismos.pages file
  (c/GET "/entries" [] pages/entries)
  (c/GET "/test" _ str) ;NOTE: _ used to suppress linter warning
  (c/GET "/testauth" req (if (:identity (:session req)) "buh" (str req)))
  (c/GET "/login" [] pages/login)
  (c/POST "/login" _ auth/do-login)
  ;(c/POST "/login" _ str) ;This bih is for testing what is posted
  (c/GET "/logout" _ auth/do-logout)
  (c/POST "/logout" _ auth/do-logout)
  (route/not-found "Not Found"))

;Create an app with anti-forgery turned on
(def app
  ;(wrap-defaults app-routes site-defaults))
  ;(wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] true)))
  (-> 
    (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] true))
    (wrap-authentication auth/backend)))
