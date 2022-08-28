# Tour of Aya

## Basic language features

Aya is a stack based language.

```
aya> 1 1 +
2
aya> .# This is a line comment
aya> 1 2 + 10 * 3 / 10 -
0
```

Generally, most symbols that are not a lowercase letter are an operator (Including uppercase letters).  Extended operators come in the form `.*`, `M*`, `:*`,  where `*` is *any* character. Aya has many cool operators. For example:

  - Levenshtein distance (`^`)

```
aya> "kitten" "sitting" ^
3
```

  - Create range (`R`) and reshape (`L`)

```
aya> 9 R
[ 1 2 3 4 5 6 7 8 9 ]
aya> 9 R [3 3] L
[ [ 1 2 3 ] [ 4 5 6 ] [ 7 8 9 ] ]
```

  - List primes up to N (`Mp`)

```
aya> 30 Mp
[ 2 3 5 7 11 13 17 19 23 29 ]
```




  - Split string using regex (`|`)
```
aya> "cat,dog, chicken ,pig" "\\W*,\\W*" |
[ "cat" "dog" "chicken" "pig" ]

```




  - The *Apply* (`#`) operator is special in that it is parsed as an infix operator which can take another operator (or block) on its right (in this case length (`E`)) and apply to each item in the list

```
aya> 9 R [3 3] L #E
[3 3 3]
aya> 9 R [3 3] L #{E 1 +}
[4 4 4]
```

   - Many operators are broadcasted automatically however. For example: the square root (`Mq`), addition (`+`), and multiplication (`*`) operators.

```
aya> [4 16 64 ] Mq
[ 2 4 8 ]
aya> [1 2 3] 1 +
[ 2 3 4 ]
aya> [1 2 3] [10 20 30] *
[ 10 40 90 ]
```

Aya has many types of objects. The `:T` operator is used to get the type. It returns a *Symbol* (`::symbol_name`)

```
aya> aya> 1 :T
::num
aya> [1 2 3] :T
::list
aya> [1 2 3] :T :T
::sym
aya> [ 1 [1 2 3] "hello" 'c {, 1:x } {2+} ::red ] #:T
[ ::num ::list ::str ::char ::dict ::block ::sym ]
```

Lowercase letters and underscores are used for variables. The colon (`:`) operator is used for assignment. Like the apply operator (`#`), it is one of the few *"infix"* operators.

```
aya> .# Objects are left on the stack after assignment
aya> "Hello" :first
"Hello"
aya> .# The ; operator pops and disgards the top of the stack
aya> "world!" :second ;
```

As seen above, almost everything else, including all uppercase letters, is an operator. The `:P` operator will print the item on the top of the stack to stdout.

```
aya> first " " + second + :P
"Hello world!"
```

Aya supports string interpolation.

```
aya> "$first from Aya! 1 + 1 is $(1 1 +)"
"Hello from Aya. 1 + 1 is 2"
```

Blocks (`{...}`) are first class objects. They can be evaluated with the eval  (`~`) operator.

```
aya> 1 {1 +}
1 {1 +}
aya> 1 {1 +} ~
2
```

When a block is assigned to a variable, it will be automatically evaluated when the variable is de-referenced. This allows the creation of functions.

```
aya> {2*}:double
{2 *}
aya> 4 double
8
aya> .# Wrap in additional set of braces to assign as a block
aya> {{1 +}}:foo
{{1 +}}
aya> foo
{1 +}
aya> 10 foo ~
11
```

Blocks may have arguments and local variables. In the example below, `a`, `b`, and `c` are arguments and `x` and `y` are local variables. Variables that are not declared local will use the next outer scope that declares it as local. If no outer scope declares the variable, it will be assigned in the global scope.

```
aya> {a b c : x y,
         a 2 * :x; .# local
         b 3 * :y; .# local
         a 1 + :a; .# arguments are local as well
         x y + :z; .# not local, assign global variable z

         [a b c x y z] .# return a list with vars inside
     }:myfun;
```

The following will call `myfun` and assign 1 to `a`, 2 to `b`, and 3 to `c` within the scope of the function.

```
aya> 1 2 3 myfun
[2 2 3 2 6 8]
aya> .# a b c x y & z are no longer in scope
aya> a
ERROR: Variable a not found
aya> x
ERROR: Variable x not found
aya> z
8
```

Block headers may include type assertions and local variable initializers. Local variable initializers must be literal constants. By default all local variables are initialized to `0` (see `y` in the example below).

```
aya> {a::num b::str : x(10) y z("hello"),
         [a b x y z]
     }:myfun;
aya> 1 "cats" myfun
[1 "cats" 10 0 "hello"]
aya> "dogs" "cats" myfun
TYPE ERROR: {ARGS}
    Expected: ::num
    Received: "dogs"
```

Aya also supports dictionaries. `{,}` creates an empty dictionary. `.` is used for dictionary access and `.:` is used for assignment.

```
aya> {,} :d
{,
}
aya> 3 d.:x
{,
  3:x;
}
aya> d.x
3
aya> .# Keys can also be assigned in the literal itself
aya> {, 3:x; }
{,
  3:x;
}
```

Dictionaries also support metatables.
```
aya> {, {v, v.x v.y +}:add; "hello":foo; } :mtable;
aya> {, 1:x 6:y }:point;
{, 1:x; 6:y; }
aya> mtable point.:__meta__; .# Set mtable as point's metatable
```

Keys from metatables are available but hidden.

```
aya> point
{, 1:x; 6:y; }
aya> point.x point.y point.foo
1 6 "hello"
aya> point.add
7
```

Metatables can be used to create user-defined types. The `struct` macro creates a `point` metatable. The `!` operator calls the `point` constructor which creates a new table with `x` and `y` values and sets itself as the new table's metatable. *See the wiki for more details.*

```
aya> struct point {x y}
aya> 3 4 point! :p
( 3 4 ) point!
aya> [1 "hello" p] #:T
[::num ::str ::point]
aya> {self, self.x 2* self.y 2* self!} point.:double;
aya> 3 4 point!:p;
aya> p.double
( 6 8 ) point!
```

Aya also supports operator overloading for many operators. Type `\? overloadable` in the Aya interpreter to get a list of all overloadable operators.

```
aya> {other self,
         other.x self.x +
         other.y self.y +
         self!
     } point.:__add__;
aya> 3 4 point! 5 6 point! +
( 8 10 ) point!
```

The Aya core language supports many other cool things such as **closures**, built-in **fraction** and **arbitrary precision** numbers, **macro-like functions** *(the `struct` keyword above is defined completely in aya!)*, **exception handling**,  built in **plotting** and **GUI dialogs**, **list comprehension**, and ***more***!