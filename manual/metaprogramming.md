# Metaprogramming


## Blocks

Aya provides a basic data structure for representing code called a *block*. A block is a list of instructions. Internally, every Aya program is a block.

```
aya> {1 1 +}
{1 1 +}
```

Evaluate it with the `~` operator

```
aya> {1 1 +} ~
2 
```

By default, blocks assigned to variables are automatically evaluated when de-referenced. Use `` .` `` to get the block without evaluating it.

```
aya> {1 1 +} :a
{1 1 +}
aya> a
2
aya> a.`
{1 1 +}
```

Split a block into parts using the `.*` operator. 

```
aya> {3 4 *} .*
[ {3} {4} {*} ]
```

The same operator is used to join a list into a block:

```
aya> [ {3} {4} {*} ] .*
{3 4 *} 
```

`.*` automatically converts data into instructions

```
aya> [ 3 4 {*} ] .*
{3 4 *}
````

For example, `make_adder` is a function that takes a number `N` and creates a block of code that adds `N` to its input

```
aya> { {+} J .* }:make_adder
{{+} J .*} 
aya> 5 make_adder :add_five
{5 +} 
aya> 4 add_five
9
```

## Macros

In Aya, programs are evaluated from left to right

```
aya> 1 2 +
3
aya> 1 2 + 4 *
12
```

Above, the `+` and `*` operators read data from their left. When evaluating `+`, everything to the left is considered **data** and everything to the right is considered **instructions**.

```
     1 2 + 4 *
<-- data | instructions -->
```

All standard operators and functions operate only on *data*; that is, things to their left.

A macro is a function that operates on *instructions*; or things to its right. Macros may also operate on *data* and *instructions*.

For example, `struct` is a macro that reads two instructions: the type name and the list of member variables.

```
aya> struct point {x y}
<type 'point'> 
```

`if` is a macro that reads 3 instructions to achieve behavior similar to `if` keywords from imperitive languages

```
aya> if (1) {"true!"} {"false!"}
"true!"
```

The ``:` `` operator is used to create macros. It takes 2 *data* arguments. A block `B` and an integer `N`. When evaluated, it will wrap each of the next `N` *instructions* in a block (converting them to *data*) then wrap the whole thing in a list. Then it will run `B` after the newly created block.

```
aya> { "data block" } 1 :` instruction 
[ {instruction} ] "data block" 
aya> {1} 2 :` 3 +
[ {3} {+} ] 1 
```

### Macro Example

Lets define a macro `apply` that applies the instruction after it to each element of a list.

```
aya> ["three" "two" "one"] apply .upper
[ "THREE" "TWO" "ONE" ]
```

First we use ``:` `` to capture the instruction we want to apply then use the `~` operator to unwrap the instruction list

```
aya> ["three" "two" "one"] { } 1 :` .upper
[ "three" "two" "one" ] [ {.upper} ] 
aya> ["three" "two" "one"] { ~ } 1 :` .upper
[ "three" "two" "one" ] {.upper} 
```

We use the map operator `O` to apply the block to each element of the list

```
aya> ["three" "two" "one"] { ~ O } 1 :` .upper
[ "THREE" "TWO" "ONE" ]
```

Now we can replace `.upper` with the reverse operator `U` to reverse the strings in the list instead

```
aya> ["three" "two" "one"] { ~ O } 1 :` U
[ "eerht" "owt" "eno" ]
```

Finally, we can remove our example data and define our macro.

```
aya> { { ~ O } 1 :` } :apply
{{~ O} 1 :`}
```

Usage:

```
aya> ["three" "two" "one"] apply .upper
[ "THREE" "TWO" "ONE" ] 
aya> ["three" "two" "one"] apply U
[ "eerht" "owt" "eno" ] 
aya> ["three" "two" "one"] apply .[0]
"tto"
```

Apply multiple instructions by wrapping them in `()`

```
aya> ["three" "two" "one"] apply ("!" +)
[ "three!" "two!" "one!" ] 
```
