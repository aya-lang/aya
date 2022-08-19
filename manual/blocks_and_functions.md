# Blocks
Blocks contain expressions. They can be used to define functions, map instructions to lists, etc. They are denoted using curly braces `{}`.

```
{20 50 +}
```
They can be evaluated by using the `~` operator.

```
{20 50 +}~ .# => 70
```

When blocks are evaluated, their contents are dumped to the stack and the stack continues as normal. This is what happens when we call functions as well.

```
100 10+ {1 + 2 *}~
    110 {1 + 2 *}~
       110 1 + 2 *
           111 2 *
               222
```

## Block Header

A comma (`,`) is used to specify that the block has a header. Anything before the comma is considered the header and everything after is considered the instructions. A block header is used to introduce local variables to the block in the form of arguments or local declarations. Arguments and declarations are separated by a colon (`:`). Arguments must go on the left hand side of the colon and local declarations on the right.

```
{<arg1> <arg2> … <argN> : <local dec 1> ... <local dec M>, <block body>}
```

If no colon is included in the header, all variable names will be used as arguments.

```
{<arg1> <arg2> … <argN>, <blovk body>}
```

If a colon is the first token in a block header, all variable names are considered local declarations.

```
{: <local dec 1> <local dec 2> ... <local dec M>, <block body>}
```

Finally, if nothing is included in the block header, the block will be parsed as a dictionary.

```
{, <dict body>}
```

## Arguments

Arguments work like parameters in programming languages with anonymous/lambda functions. Before the block is evaluated, its arguments are popped from the stack and assigned as local variables for the block.

```
aya> 4 {a, a2*}~
8
```

Arguments are popped in the order they are written.

```
aya> 8 4 {a b, [a b] R}~
[8 7 6 5 4]
```

Arguments are local variables.

```
aya> 2:n 3{n, n2^}~ n
2 9.0 2
```

## Argument Type Assertions

Arguments may have type assertions. Write a variable name followed by a symbol corresponding to the type.

```
1 2 {a::num b::num, a b +}~    .# => 3
"1" 2 {a::num b::num, a b +}~   .# TYPE ERROR: Type error at ({ARGS}):
                             Expected (::num)
                             Recieved ("1" )
```

If a user defined type defines a `type` variable as a symbol. The symbol will be used for type assertions.

```
{,
  ::vec :type;

  ... define vec variables and functions ...
}:vec;

{v::vec,
  v :P
}:printvec;
```

The type checker will use the `.type` variable:

```
aya> 1 2 vec! :v
<1,2>
aya> v printvec
<1,2>
aya> 3 printvec
TYPE ERROR: Type error at ({ARGS}):
	Expected (::vec)
	Recieved (3 )
```


## Local Declarations

Local declarations introduce a local scope for that variable. Scope is discussed in greater detail in the Variable Scope section of this document. Local declarations can not have type declarations.


```
aya> "A":a
"A"
aya> a println {:a, "B":a; a println}~ a println
A
B
A
```

All local declarations default to the value 0.

```
{: a, "a is $a" :P } ~
```

Change the default value for a local variable using an initializer.

```
aya> {: a(10) b c("hello") d([1 2]), [a b c d] } ~
[ 10 0 "hello" [ 1 2 ] ]
```

Variables are initialized before run time and therefore can not be variables.

```
aya> 99 :l
99

aya> {: a(l), a} ~
SYNTAX ERROR: Block header: Local Variables Initializer: Must contain only const values

aya> .# define a as a function which evaluates to l
aya> {: a({l}), a} ~
99

aya> .# define a as a list which evaluates to l
aya> {: a([l]), a} ~
[ 99 ]
```

## Keyword Arguments

Aya provides a way to use keyword arguments using dictionaries and local declarations. Consider the following function:

```
{kwargs::dict : filename("") header dtype(::num),
  kwargs .W

  "filename=\"$filename\", header=$header, dtype=$dtype" :P
}:fn;
```

The function `fn` contains 1 argument `kwargs` (the name can be anything) and three local declarations. The operator `.W` will export variables from the `kwargs` dict only if they are defined in the local scope. This means that any variables defined in `kwargs` will overwrite the initialized local variables. Every variable not given by `kwargs` dict will remain in its default state.

```
aya> {, "sales.csv":filename 1:header} fn
filename="sales.csv", header=1, dtype=::num

aya> .# The variable `useless` does not exist in the local scope of `fn`
aya> .#    and will therefore be ignored
aya> {, "colors.csv":filename "blah":useless} fn
filename="colors.csv", header=0, dtype=::num

aya> {, "names.csv":filename ::str:dtype} fn
filename="names.csv", header=0, dtype=::str

aya> {, } fn
filename="", header=0, dtype=::num
```

# Functions

We now have the basic building blocks for defining functions: variable assignment and blocks. A function is simply a variable that is bound to a block. When the variable is called, the interpreted dumps the contents of the block onto the instruction stack and then continues evaluating. Functions can take advantage of anything that a normal block can including arguments and argument types.

Here are a few examples of function definitions:
`swapcase` takes a character and swaps its case.

```
aya> {c::char, c!}:swapcase;
aya> 'q swapcase
'Q'
```

Below is the definition of the standard library function roll, This function will move the last element of a list to the front.

```
aya> {B\.V}:roll;
aya> [1 2 3 4] roll;
[4 1 2 3]
```

When used with block arguments, functions can be written in very readable ways. The following function swapitems takes a list and two indices and swaps the respective elements. It uses block arguments and type assertions.

```
{listL i::num j::num : tmp,
  list i I : tmp;
  list j I  list i D
  tmp list j D
  list
}:swapitems;

aya> [1 2 3 4 5] 0 3 swapitems
[ 4 2 3 1 5 ]
```

To see more examples check out the standard library located at /base/std.aya
