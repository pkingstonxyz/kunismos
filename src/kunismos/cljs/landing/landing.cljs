(ns kunismos.cljs.landing.landing)

;(enable-console-print!)
;(prn "Hello clojurescript!")

(defn random-chars
  "Generates the specified n number of characters"
  [n]
  (reduce str (repeatedly n #(char (+ 32 (rand-int 95))))))

(defn glitchify-text 
  "A function to actually set the text to glitch"
  [e]
  ;(prn "ran once")
  (let [numChars (count (.-innerText e))]
    (set! (.-innerText e) (random-chars numChars))))

(defn reset-up-to
  "A function that resets the string up to a given point and makes the rest random"
  [point
   string]
  (str (subs string 0 point) (random-chars (- (count string) point))))

(defn glitchtext
  "A function to make text glitch"
  [e]
  (let [duration 60 
        repetitions 10 
        endTime (* duration repetitions)
        originalText "κυνισμός"]
    ;(prn originalText)
    (dotimes [timeScalar repetitions] ;Garble the text for awhile
      (js/setTimeout #(glitchify-text e) (+ duration (* timeScalar duration))))
    ;(js/setTimeout #(set! (.-innerText e) (str originalText)) (+ duration (* repetitions duration)))
    (dotimes [round (inc (count originalText))]
      (js/setTimeout #(set! (.-innerText e) (reset-up-to round originalText)) (+ endTime (* round duration))))
  ))

(let [el (.getElementById js/document "title")]
  (set! (.-onmouseover el) #(glitchtext el)))
;(let [element (.getElementById js/document "title")]
;  (.setAttribute element "onmouseover" (this-as this #(glitchtext this))))
