 (ns defoclock)
 
 
(defmacro deflet [bindings & body]
  `(let ~bindings
     ~@(for [v (map first (partition 2 (destructure bindings)))]
         (list 'def v v))))
 
 