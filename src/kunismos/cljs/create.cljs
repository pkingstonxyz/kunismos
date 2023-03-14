(ns kunismos.cljs.create)

(enable-console-print!)

(prn "Hello clojurescript!")

(defn ctrl+enter-submit-handler 
  [e]
  ;(let [k (.-key e)]
    ;(prn k)))
  (prn (.-keyCode e)))
(js/document.addEventListener "keydown" ctrl+enter-submit-handler)
