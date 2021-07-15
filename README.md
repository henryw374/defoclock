# defoclock

For when it's time to start def'ing stuff in the REPL

```
[henryw374/defoclock "0.1.1"]
```

## Comparison

[scope-capture](https://github.com/vvvvalvalval/scope-capture) is more comprehensive and featured than this, so 
definitely check that out. I didn't realise that existed when I made this and I only use that lib now.

## Rationale

You've just hammered out a function and it compiled

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

Now what? Well, for example you might proceed like this:
 
Start mentally executing the function. Get the full stack trace for the line number to help
 narrow things down a bit too. Let's say nothing jumps out at you as wrong, 
 or changes you think might fix it don't work.
 
Another strategy would be try to start narrowing the problem space down, (see [Debugging with Scientific Method](https://www.youtube.com/watch?v=FihU5JxmnBg))
 for example. Let's try that:
 
Switch to the namespace of the function and start def'ing stuff ...

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
(use 'widdindustries.defoclock)

```

or in a cljs repl

```
(require '[widdindustries.defoclock :refer-macros [dlet dloop dfor ddoseq ddefn]])
```

Now change your `(let  [...])` to `(dlet  [...])` and evaluate the `(dlet [...]` form in the repl. Pro-tip: Your 
IDE will have built-in commands to send forms to your repl for evaluation

Boom! You now have `x`, `y` and `z` def'd in your repl. Letting you just evaluate the 
`{y (apply + x z)}` bit from your function, and then keep going on smaller bits until the problem becomes apparent 

It is possible that the evaluation blows up before all the local variables have been defined. Consider:

```
(dlet [x (/ 1 0)]
  (inc x))
```

but in that case, just go through your local variables in order, evaluatiing them at the repl to check
they are what you think they should be. So for example with this form above, eval `x` at the repl, you'll
see it is undefined, so it points to the problem being there.

Seasoned Clojurians will already know this, but to avoid your namespaces getting cluttered with the vars
produced by defoclock, use the [Reloaded Workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
or in Clojurescript, reload your browser or other environment.

## Basic Usage

```
[henryw374/defoclock "0.1.1"]
```

In Clojure

```
(use 'widdindustries.defoclock)
```

or, so it also works in a cljs repl

```
(require '[widdindustries.defoclock :refer-macros [dlet dloop dfor ddoseq ddefn] :refer [dlet dloop dfor ddoseq ddefn]])
```

Pro-tip: Bind this command to a key in your IDE so you can instantly bring in defoclock anytime

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
