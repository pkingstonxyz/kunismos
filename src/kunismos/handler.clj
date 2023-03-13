(ns kunismos.handler
  (:require [compojure.core :as c]
            [compojure.route :as route]
            ;[compojure.coercions :as coerce] ;Coercions
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.anti-forgery]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [kunismos.pages :as pages]
            [kunismos.auth :as auth]))

;; The gateway function

(defn gateway 
  "An auth gateway that directs the user depending on authorization"
  [request success failure]
  (if (auth/authenticated? request)
    success
    failure))

;; Ring/Compojure stuff


(c/defroutes app-routes
  (c/GET  "/" req (gateway req pages/create pages/landing))
  (c/GET  "/landing" _ pages/landing)
  (c/GET  "/create" req (gateway req pages/create (redirect "/landing?naughty=true")))
  (c/POST "/create" req (gateway req pages/create-post (redirect "/landing?naughty=true" 303)))
  (c/GET  "/journal" req (gateway req pages/journal (redirect "/landing?naughty=true")))
  (c/GET "/login" _ pages/login)
  (c/POST "/login" _ auth/do-login)
  (c/ANY "/logout" _ auth/do-logout)
  (c/GET "/createaccount" _ pages/createaccount)
  (c/POST "/createaccount" _ auth/do-create-user)
  (route/not-found "Not Found"))
;(c/defroutes app-routes
  ;(GET "/" [x :<< coerce/as-int, y :<< coerce/as-int] (str (+ x y))) ;This is an example of using query params and using coercion
  ;(c/GET "/" [] pages/create)
  ;(c/POST "/" [text] (pages/add-post text)) ;I'm doing it like this (function call instead of function) so I don't have to destructures the request in the kunismos.pages file
  ;(c/GET "/entries" [] pages/entries)
  ;(c/GET "/test" _ str) ;NOTE: _ used to suppress linter warning
  ;(c/GET "/testauth" req (if (:identity (:session req)) "buh" (str req)))
  ;;NOTE: These are done (I think?)
  ;(c/GET "/login" [] pages/login)
  ;(c/POST "/login" _ auth/do-login)
  ;;(c/POST "/login" _ str) ;This bih is for testing what is posted
  ;(c/GET "/logout" _ auth/do-logout)
  ;(c/POST "/logout" _ auth/do-logout)
  ;(c/GET "/createaccount" [] pages/createaccount)
  ;(c/POST "/createaccount" _ auth/do-create-user)
  ;(route/not-found "Not Found"))

(def my-site-defaults
  (-> site-defaults
      (assoc-in [:security :anti-forgery] true)
      (assoc-in [:session  :store] (cookie-store {:key (byte-array 16 [0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15])}))
      ))

;Create an app with anti-forgery turned on
(def app
  ;(wrap-defaults app-routes site-defaults))
  ;(wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] true)))
  (-> 
    (wrap-defaults app-routes my-site-defaults)
    (wrap-authentication auth/backend)
    (wrap-authorization auth/backend))) ;Not sure if I actually need this one
