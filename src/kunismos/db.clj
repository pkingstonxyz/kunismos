(ns kunismos.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))


;NOTE: Doing all of this database stuff was pretty confusing, especially in regards to when/where I create my tables. I got it figured out though. The tables are created and dropped via migrations! I'm just doing everything in the REPL though.
; Define the database
(def db {:dbtype "sqlite" :dbname "kunismos.sqlite"})

; Create the datasource
(def ds (jdbc/get-datasource db))

;TODO: Think about how to set up the tables for quick querying andwriting (like what needs a foreign key for what.) Perhaps write it in Django and look at the SQL it generates? Who is to say.
;I used this in the repl to create the table
;(jdbc/execute! ds ["drop table kunismos"])
;(jdbc/execute! ds ["CREATE TABLE kunismos (user_id integer, text text, date datetime default current_timestamp);"])
;(jdbc/execute! ds ["select user_id from kunismos"] {:return-keys false :builder-fn rs/as-unqualified-lower-maps})
;(jdbc/execute! ds ["select id from users"])
;
;(let [text "patrick"] (jdbc/execute! ds [(str "insert into kunismos(text) values('" text "')")]))

;NOTE: my sql queries aren't complex enough to warrant learning a fancy library like honeysql
;Lesson learned: Don't let complexity creep
(defn get-entries [userid]
  (jdbc/execute! ds [(format "select * from kunismos where user_id=%d order by date desc" userid)] {:return-keys true :builder-fn rs/as-unqualified-lower-maps}))

(defn create-entry [text uid]
  (jdbc/execute! ds [(format "insert into kunismos(user_id,text) values(%d,\"%s\")" uid text)]))
