 (ns defoclock)
 
 (defn- locals [bindings]
   (->>
     bindings
     (partition 2)
     (filter (comp not keyword? first))
     (apply concat)
     (destructure)
     (partition 2)
     (map first )))
 
 (defn- dodefs [bindings]
   (for [v (locals bindings)]
     (list 'def v v)))
 
(defmacro dlet [bindings & body]
  (vec (for [[d v] (->> bindings destructure (partition 2))]
         (list 'def d v))))

(defmacro dloop [bindings & body]
  `(dlet ~bindings))
 
 (defmacro dfor [bindings & body]
   `(take 1
      (for ~bindings
        ~(dodefs bindings))))

(defmacro ddoseq [bindings & body]
  `(take 1
     (for ~bindings
       ~(dodefs bindings))))
 
 (defmacro ddefn [args fn-name arg-decls & more]
   `(let ~(vec (interleave arg-decls args))
      ~@(for [v (->> (destructure (vec (interleave arg-decls args)))
                     (partition 2)
                     (map first))]
         (list 'def v v))))
 
 