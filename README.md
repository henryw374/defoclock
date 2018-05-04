# defoclock

For when it's time to start def'ing stuff in the REPL

## Rationale

You've just hammered out a function and it compiles

```
(defn foo [bar]
  (let [x 1
        y (assoc bar :key x)
        z (.. Runtime getRuntime availableProcessors)]
    {y (apply + x z)}))
```

with great bravado, you try to run it

```
(foo {})
```

but oh no! There is a problem

```
IllegalArgumentException Don't know how to create ISeq from: java.lang.Integer  clojure.lang.RT.seqFrom (RT.java:550)
```

Now what? Well there are several options
 
1) Start mentally executing the function. Get the full stack trace for the line number to help
 narrow things down a bit too. Well, let's say nothing jumps out at you so you start on 
 option 2.
 
2) Switch to the namespace of the function and start def'ing stuff ...

```
(in-ns 'foo)
(def bar {})

```

now execute the fn body `(let [...])` in the repl
 
Same issue. Right, now start def'ing the let bindings one by one
 
```
(def x 1)
(def y (assoc bar :key x))
...
``` 

and so on.

This is the process of narrowing down the code you execute until you 
finally realize what the problem is 

```
(apply + x z)
```

should just be 
```
(+ x z)
```

Awesome! 

Defoclock can be used to speed things up here. There are various macros which will def stuff in your
current ns. 

```
(use 'defoclock)

```

Now change your `(let  [...])` to `(dlet  [...])` and evaluate the `dlet` form in the repl.

Boom! You now have `x`, `y` and `z` def'd in your repl. Letting you just evaluate the 
`{y (apply + x z)}` bit from your function, and then keep going on smaller bits until the problem becomes apparent 


## Usage

```
[henryw374/defoclock "0.1.0"]
```

```
(use 'defoclock)
```

Provides equivalent of clojure.core local binding macros: dlet, dloop, dfor, ddoseq

Also provides an equivalent of defn, so you can def a functions args:

```
(defn my-func [x {:keys [y z}] (identity x) )

```

Changes to:

```
(ddefn [1 {:y 2 :z 3}] my-func [x {:keys [y z]}] (identity x) )

```

So you change defn to ddefn and add a vector of actual arguments you want to pass to the function.
Instead of running the function, it will def the parameters with the values of the arg list.


## License

Copyright © 2018 Widd Industries Ltd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
