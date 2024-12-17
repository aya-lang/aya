# Syntax Overview

## Execution

Aya is a stack based language. Execution flows from left to right

```
aya> 1 2 +
3 
aya> 1 2 + 4 *
12 
aya> 1 2 + 4 * 3 /
4 
```


## Comments

### Line Comments

Line comments begin with `.#`

```
aya> aya> .# comment
aya> 1 .# comment
1 
aya> .#leading space optional
```

### Block Comments

Block comments start with `.{` and end with `.}`

```
.{ This is a
    block comment!  .}
```

```
.{ 
    Also a block comment
.}
```

```
aya> .{ block .{ comments cannot be .} nested .}
SYNTAX ERROR: .} is not a valid operator
```

## Variables

Use `:varname` to assign a variable. Use the plain variable name to access

```
aya> 1 :x
1 
aya> x
1 
```

Single characters are supported

```
aya> 1 :α
1 
aya> α
1 
```

Any string of lowercase letters and underscores can be used as a variable.

```
aya> 1 :this_is_a_valid_variable
1
```

Any string of characters can be used as a variable if the literal is quoted. They cannot be accessed directly. These types of variables are mostly useful for [dictionaries](#dictionaries).

```
aya> 1 :"Quoted Variable!"
1 
aya> "Quoted Variable!" :S~
1 
```

Numbers and uppercase letters cannot be used for variables

```
aya> 5 :MyVar0
Unexpected empty stack while executing instruction: :M
   in :M .. y V ar 0}
```

### Special Variables

Double leading and trailing underscores are used for special variables

*See operator overloading and metatables for examples*


## Numbers

*Main Page: [Numbers](./number.md)*

### Integers & Decimals

```
aya> 1
1 
aya> 1.5
1.5 
aya> .5
.5
```

### Negative Numbers

`-` is parsed as an operator unless immediately followed by a number
```
aya> 1 2 - 3
-1 3 
aya> 1 2 -3
1 2 -3 
```

`:` can also be used to specify a negative number

```
aya> 1 2 :3
1 2 -3
```

### Big Numbers

Arbitrary precision numbers have the form `:Nz`

```
aya> :123456789012345678901234567890z
:123456789012345678901234567890z
aya> :3.141592653589793238462643383279502884197169399z
:3.141592653589793238462643383279502884197169399z
```

### Hexadecimal Literals

Hexadecimal literals have the form `:0xN`

```
aya> :0xfad
4013
```

If the hexadecimal does not fit in a standard integer, it will automatically be promoted to a *big number*.

```
aya> :0xdeadbeef
:3735928559z
```


### Binary Literals

Binary literals have the form `:0bN`

```
aya> :0b1011
11 
```

If the literal does not fit in a standard integer, it will automatically be promoted to a *big number*.

```
aya> :0b1011101010101001010101001010101010001011
:801704815243z 
```

### Scientific/"e" Notation

Number literals of the form `:NeM` are evaluated to the literal number `N * 10^M`.

```
aya> :4e3
4000
aya> :2.45e12
2450000000000
aya> :1.1e-3
.0011
```

### Fractional Numbers

Fractional literals have the form `:NrM`

```
aya> :1r2
:1r2
aya> :3r
:3r1
aya> :-1r4
:-1r4 
```

### PI Times

Number literals of the form `:NpM` are evaluated to the literal number `(N * PI)^M`. If no `M` is provided, use the value 1.

```
aya> :1p2
9.8696044
aya> :1p
3.14159265
aya> :3p2
88.82643961
```

### Root Constants

Number literals of the form `:NqM` are evaluated to the literal number `N^(1/M)`. The default value of M is 2.

```
aya> :2q
1.41421356
aya> :9q
3
aya> :27q3
3
```

### Complex numbers

Complex numbers are built in. `:NiM` creates the complex number `N + Mi`. Most mathematical operations are supported 

```
aya> :-1i0
:-1i0 
aya> :-1i0 .^
:0i1 
aya> :3i4 Ms
:3.85373804i-27.01681326
aya> :3i4 Mi .# imag part
4 
aya> :3i4 Md .# real part
3 
```

### Number Constants

constants follow the format `:Nc`

| number | value |
|:---:|:---|
| `:0c` | pi |
| `:1c` | e |
| `:2c` | double max |
| `:3c` | double min |
| `:4c` | nan |
| `:5c` | inf |
| `:6c` | -inf |
| `:7c` | int max |
| `:8c` | int min |
| `:9c` | char max |

## Characters

*Main Page: [Characters & Strings](./characters_and_strings.md)*

### Standard Characters

Characters are written with a single *single quote* to the left of the character:

```
aya> 'a
'a 
aya> '   .# space character
'  
aya> ''  .# single quote character
'' 
aya> 'ÿ  .# supports unicode
'ÿ 
```

### Hex Character Literals

Hex literal characters are written using a `'\x___'` and **require closing quotes**.

```
aya> '\xff'
'ÿ 
aya> '\x00a1'
'¡ 
```

### Named Character Literals

Many characters have names. All names consist only of lowercase alphabetical characters. Use `Mk` operator to add new named characters.

```
'\n'         .# => <newline>
'\t'         .# => <tab>
'\alpha'     .# => 'α'
'\pi'        .# => 'π'
```

## Strings

*Main Page: [Characters & Strings](./characters_and_strings.md)*

### Standard String Literals

String literals are written with double quotes (`"`):

```
aya> "Hello, world!"
"Hello, world!" 
```

Use `\\` to escape to double quotes. (string printing in the REPL will still display the escape character)

```
aya> "escape: \" cool"
"escape: \" cool" 
aya> "escape: \" cool" println
escape: " cool
```

Strings may span multiple lines.

```
"I am a string containing a newline character
	and a tab."
```

### Special Characters in Strings

Strings can contain special characters using `\{___}`. Brackets can contain named characters or Unicode literals.

```
"sin(\{theta}) = \{alpha}"    .# => "sin(θ) = α"
"\{x00BF}Que tal?"            .# => "¿Que tal?"
```

### String Interpolation

Use `$` for string interpolation

```
aya> 10 :a;
aya> "a is $a"
"a is 10" 
```

Use `$(...)` for expressions

```
aya> "a plus two is $(a 2 +)"
"a plus two is 12"
```

Use `\` to keep the `$` char

```
aya> 10:dollars;
aya> "I have \$$dollars."
"I have $10"
```

If used with anything else, keep the `$`

```
aya> "Each apple is worth $0.50"
"Each apple is worth $0.50"
```

### Long String Literals

Use triple quotes for long string literals.

```
"""This is
a long string
literal"""
```


No escape characters or string interpolation is processed

```
aya> """This is a long string literal $foo \{theta}"""
"This is a long string literal $foo \{theta}"
```

## Symbols

Symbols are primarily used for metaprogramming. Symbols are any valid variable name starting with `::`

```
aya> ::my_symbol
::my_symbol 
```

Symbols can be any string if single quotes are used immediately after the `::`

```
aya> ::"My Symbol"
::"My Symbol"
```


## Lists

*Main Page: [Lists](./lists.md)*

### List Literals

Lists are written with square brackets (`[]`) and must not contain commas. They may contain any data type:

```
aya> [1 2 3]
[ 1 2 3 ]
aya> []
[ ]
aya> [1 2 "Hello" [3 4]]
[ 1 2 "Hello" [ 3 4 ] ]
```

Lists may also contain expressions:

```
aya> [1 2 + 3 4 +]
[ 3 7 ]
```

### List Stack Captures

Use `[N| ... ]` to capture items off the stack into the list

```
aya> 9 [1| 8 7 6]
[ 9 8 7 6 ] 
aya> 10 9 [2| 8 7 6]
[ 10 9 8 7 6 ] 
aya> 10 9 [2|]
[ 10 9 ] 
```

### List Comprehensions

*See [list comprehensions](./lists.md)*

### Indexing

#### Get a value from a list

Use `.[ (index) ]` to get a value from a list

```
aya> [1 2 3 4] :list
[ 1 2 3 4 ] 
aya> list.[0]
1 
aya> list.[:-1]
4 
```

#### Set a value at an index in a list

Use `(value) (list) .[ (index) ]` to set a the value in a list at an index

```
aya> [1 2 3 4] :list
[ 1 2 3 4 ] 
aya> 10 list.:[0]
[ 10 2 3 4 ]
```

## Dictionaries

*Main Page: [Dictionaries and User Types](./dictionaries.md)*

### Dictionary Literals

Dictionary literals have the form `{, ... }`. All variables assigned between `{,` and `}` are assigned to the dictionary

```
aya> {, 1:a 2:b }
{,
  2:b;
  1:a;
}
```

`{,}` creates an empty dict

```
aya> {,}
{,}
```

### Getting Values

Use dot notation to get values from a dict:

```
aya> {, 1:a 2:b } :d
{,
  2:b;
  1:a;
} 
aya> d.a
1 
aya> d .b
2
```

Or use strings or symbols with index notation (`.[]`)

```
aya> d.["a"]
1 
aya> d.[::a]
1
```

Or use `:I` operator

```
aya> d ::a I
1 
aya> d "a" I
1 
```

Dot notation can be used with [quoted variables](#variables)

```
aya> {, 1:"Hello, world!" } :d
{,
  1:"Hello, world!";
} 
aya> d."Hello, world!"
1 
```


### Setting Values

Use `.:` notation to set values of a dict

```
aya> {,} :d
{,} 
aya> 10 d.:a
{,
  10:a;
} 
```

Or using strings or symbols with index notation (`.:[]`)

```
aya> 11 d.:["b"]
{,
  11:b;
  10:a;
} 
aya> 12 d.:[::c]
{,
  11:b;
  10:a;
  12:c;
} 
```

This notation can be used with [quoted variables](#variables)

```
aya> {,}:d
{,} 
aya> 10 d.:"Hello, world!"
{,
  10:"Hello, world!";
} 
```

## Blocks

*Main Page: [Blocks & Functions](./blocks_and_functions.md)*

### Basic Blocks

Use `{...}` to define a code block.

```
aya> {2 +}
{2 +}
```

If a code block is assigned to a variable, execute it immediately when the variable is accessed

```
aya> {2 +}:add_two
{2 +} 
aya> 4 add_two
6 
```

### Short Block Notation

Any set of tokens following a tick (`\``) until an operator or variable will be parsed as a block. Useful for saving a character when golfing

```
aya> `+
{+} 
aya> `1 + 1
{1 +} 1
aya> `"hello" 1 'd +
{"hello" 1 'd +}
```

This notation also terminates at variables names

```
aya> `x 1
{x} 1 
aya> `1 x 1
{1 x} 1 
```

### Block Headers

Use a comma in a block to create a block *header*. Block headers define local variables and block arguments

See [Variables and Scope](./variables_and_scope.md) and [Blocks and Functions](./blocks_and_functions.md) for more details.

If the header is empty, the block is parsed as a dict (see *Dictionary*)

```
aya> {, 1:a }
{,
  1:a;
} 
```

#### Arguments

Add arguments to a block

```
aya> {a b c, a b + c -}:foo
{a b c, a b + c -} 
aya> 1 2 3 foo
0
```

Arguments can have type assertions. The block will fail if the type does not match

```
aya> {a::num b::str, "a is $a, b is $b"}:foo
{a::num b::str, "a is $a, b is $b"} 
aya> 1 "two" foo
"a is 1, b is two" 
aya> "one" 2 foo
        {ARGS}
        Expected:::str
        Received:2
   in a::num b::str, .. "a is $a, b is $b"}
Function call traceback:
  Error in: foo

```

#### Local Variables

To declare local variables for a block, use a `:` in the header: `{: ... ,}`

```
aya> {: local_a local_b, 10:local_a 12:local_b 14:nonlocal_c} ~
10 12 14 
aya> local_a
Undefined variable 'local_a'
   in local_a .. }
aya> nonlocal_c
14
```

Use parenthesis after the local variable to set the initial value

```
aya> {: local_a(99) , local_a} ~
99
```

Use `^` after a local variable to "capture" it from the surrounding scope

```
aya> 1:a
1 
aya> {: a^, }
{: a(1),} 
```

Can mix & match locals and arguments

```
aya> 9 :captured_local
9 
aya> { arg typed_arg::str : default_locl initialized_local(10) captured_local^, }
{arg typed_arg::str : default_locl(0)initialized_local(10)captured_local(9),} 
```


## Operators

*Main Page: [Operators](./operators.md)*

### Standard Operators

All single uppercase letters except `M` are operators

```
aya> 6 R
[ 1 2 3 4 5 6 ] 
aya> 4 [5] J
[ 4 5 ] 
```

### "Dot" Operators

Most characters immediately following a dot (`.`) are an operator

```
aya> 6 .R
[ 0 1 2 3 4 5 ] 
aya> 6 .!
1 
```

#### Exceptions

| Special Case | Description |
|:---:|:---|
| `.<grave>` | Deference Without Execution |
| `.#` | [Line Comment](#line-comments) |
| `.{` | [Block Comment](#block-comments) |
| `.'` | [Symbol](#symbols) |



#### Dereference Without Executing (`.<grave>`)

`.<grave>` Dereference a variable without executing the block

```
aya> {1 2 +}:f
{1 2 +} 
aya> f
3 
aya> f.`
{1 2 +}
```

If the variable is not a block dereference it normally

```
aya> 1:a
1 
aya> a.`
1 
```


### "Colon" Operators

Most characters immediately following a  color (`:`) are an operator

```
aya> [1 2] [2] :|
[ 1 ] 
```

#### Exceptions

| Special Case | Description |
|:---:|:---|
| `:"` | [Symbol](#symbols) |
| `:{` | [Extension Operator](#extension-operators) |


### "Misc" Operators

`M` plus any character is an operator

```
aya> "Hash" M#
635696504 
aya> 0.5 Ms
.47942554 
```

### Non-Standard "Infix" Stack Operators

#### List Map (`:#`)

The `:#` operator takes a block on its *right* and maps it to the list on the stack

```
aya> [1 2 3] :# {1 +}
[ 2 3 4 ] 
```

#### List Map Shorthand (`#`)

*See [Broadcast Operator](./lists.md)*

Same as `:#` but automatically creates a block using [short block notation](#short-block-notation)


```
aya> [1 2 3] # 1 +
[2 3 4]
```

#### Capture Instructions (`:\``)

Takes a block `B` and a number `N` from the stack. Captures `N` instructions from the instruction stack.

```
aya> {P} 2 :` 1 +
"[ {1} {+} ]"
```

### Extension Operators

Extension operators have the form `:(...)`. 

```
aya> 123456789 "dd/MM/yyyy HH:mm:ss" :(date.format)
"02/01/1970 05:17:36" 
```

These operators are always wrapped in the standard library. They should almost never be used for normal development

```
aya> import ::date
aya> 123456789 date!
Jan 02, 1970 5:17:36 AM 
```


## User Types

### Struct

#### Defining A Struct

Create a struct with the following syntax:

```
struct <typename> {<member> <vars> ...}
```

For example:

```
aya> struct point {x y}
aya> point
(struct ::point [ ::x ::y ])
```

#### Create Instance Of Struct

To create an instance of a struct, use the `!` operator on the type. Member variables should exist on the stack

```
aya> struct point {x y}
aya> 1 2 point!
( 1 2 ) point!
```

#### Accessing Values of a Struct

Use standard dot notation to acces user type values

```
aya> struct point {x y}
aya> 1 2 point! :p
( 1 2 ) point!
aya> p.x
1 
aya> p.y
2 
```

#### Struct Member Functions

Use the `def` keyword to define member functions for structs

```
aya> def point::format {self, "<$(self.x), $(self.y)>"}
aya> 1 2 point! :p
( 1 2 ) point! 
aya> p.format
"<1, 2>" 
```

## Golf Utilities

### Golf Constants

Any single-character key stored in `__cdict__` can be accessed using `¢` + that character

```
aya> {, "Hello!":"!" 10:a }:__cdict__
{,
  "Hello!":"!";
} 
aya> ¢!
"Hello!" 
aya> ¢a
10
```

`golf` standard library defines many useful variables in `__cdict__`

```
aya> import ::golf
aya> ¢Q
[ "QWERTYUIOP" "ASDFGHJKL" "ZXCVBNM" ] 
aya> ¢½
[ 1 2 ] 
```
