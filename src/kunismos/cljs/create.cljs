(ns kunismos.cljs.create)

;(enable-console-print!)

;(prn "Hello clojurescript!")

(defn ctrl+enter-submit-handler 
  [e]
  (let [kCode  (.-keyCode e)
        kMeta  (.-metaKey e)
        kCtrl  (.-ctrlKey e)
        target (.-target e)]
    (when (and (= kCode 13) (or kMeta kCtrl)) 
      (.submit (.-form target)))))

(js/document.addEventListener "keydown" ctrl+enter-submit-handler)
