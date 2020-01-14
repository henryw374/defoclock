(ns widdindustries.capture)

(def form (atom nil))

(defn exec []
  (when-let [f @form]
    (let [current-ns *ns*]
      (try
        (eval f)
        (finally
          (in-ns (ns-name current-ns)))))))

(defn capt [f]
  (reset! form 
    `(do 
       (in-ns (symbol ~(str (ns-name *ns*))))
       ~f)))
