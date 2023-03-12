(defproject kunismos "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [hiccup "1.0.5"]
                 [com.github.seancorfield/next.jdbc "1.3.858"]
                 [org.xerial/sqlite-jdbc "3.41.0.0"]
                 [com.github.seancorfield/honeysql "2.4.1002"]
                 [buddy/buddy-auth "3.0.1"]
                 [buddy/buddy-hashers "1.8.158"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler kunismos.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
