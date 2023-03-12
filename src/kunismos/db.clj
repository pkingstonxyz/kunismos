(ns kunismos.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))


;NOTE: Doing all of this database stuff was pretty confusing, especially in regards to when/where I create my tables. I got it figured out though
; Define the database
(def db {:dbtype "sqlite" :dbname "kunismos.sqlite"})

; Create the datasource
(def ds (jdbc/get-datasource db))

;I used this in the repl to create the table
;(jdbc/execute! ds ["drop table kunismos"])
;(jdbc/execute! ds ["CREATE TABLE kunismos (id int auto_increment primary key, text text, date datetime default current_timestamp);"])
;
;(let [text "patrick"] (jdbc/execute! ds [(str "insert into kunismos(text) values('" text "')")]))

;NOTE: my sql queries aren't complex enough to warrant learning a fancy library like honeysql
;Lesson learned: Don't let complexity creep
(defn get-entries []
  (jdbc/execute! ds ["select * from kunismos order by date desc limit 10"] {:return-keys true :builder-fn rs/as-unqualified-lower-maps}))

(defn create-entry [text]
  (jdbc/execute! ds [(str "insert into kunismos(text) values('" text "')")]))
