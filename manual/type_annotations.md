# Type Annotations


## Get type `:T`

Use `:T` to get the type of an object:

```
aya> [1 2 3] :T
list
aya> "Hello" :T
str
aya> [1 2 3] :T
list
aya> list :T
type
aya> list :T :T
type
```

## Is instance `:@`

Use `:@` to check if something is an instance of a type:

```
aya> "Hello" str :@
1
aya> [1 2 3] list :@
1
aya> "Hello" str :@
1
aya> list type :@
1
aya> "cat" num :@
0
```

Instance operator respects inheritance for user types (more on user types below).

```
aya> class base
aya> ::derived base extend
derived
aya> derived! base :@
1
```

## Type Annotations

Type annotations have the form `parameter_name::type_name` and can only appear in a block header:

```
aya> {l::list, l E }:length
{l::list,l E}
aya> [1 2 3] length
3
aya> 3 length
Type error at argument: l::list
        Expected type: list
        Received: 3
```

## Return Types

Return types can be specified after a `->`

```
aya> {l::list -> ::num, l E}:length
{l::list, l E}
aya> [1 2 3] length
3
aya> {l::list -> ::num, "foo"}:length
{l::list -> ::num, "foo"}
aya> [1 2 3] length
Expected type num, received: "foo"
Function call traceback:
```

Return types check the size of the stack before and after execution, then checks the types of the top N values:

```
aya> {a -> ::num, 1 2}:returns_too_many
{a -> ::num, 1 2}
aya> 3 returns_too_many
Error when checking return types in {... -> ::num , ...}
Function returned 1 additional value(s):
  - 2

aya> {a -> ::num, }:missing_return
{a -> ::num,}
aya> 1 missing_return
Error when checking return types in {... -> ::num , ...}
Function missing return values. Expected stack size is 1. Current stack size is 0
```

Note that the checker uses the **size** of the stack only. A function still has access to the global stack and can make additional modifications not included in the arguments or return types and the operations would still be considered valid as long as the size of the stack matches at the end.

This function removes one extra parameter off the global stack (;) and adds an additional one it its place (99). Since the final returned values (10 and "A") match the return types, the operation is valid.


```
aya> {a b -> ::num ::str, ; 99 10 "A"}:f
{a b -> ::num ::str, ; 99 10 "A"}
aya> 0 1 2 3 f
0 99 10 "A"
```

If return type is empty, explicitly check that nothing was returned

```
aya> {a ->, }:f
{a ->,}
aya> 1 f
aya> {a ->, 10}:f
{a ->, 10}
aya> 1 f
Error when checking return types in {... -> , ...}
Function returned 1 additional value(s):
  - 10
```

A function that takes no input and produces no output:

```
aya> {->, "Hello world!":P }:f
{-> , "Hello world!" :P}
aya> f
Hello world!
```

**Important**: Return types are not checked for anonymous functions. For performance reasons, the checker relies on the call stack to take snapshots of the stack when a function is called. A new call stack frame is created only when a name is dereferenced.

```
.# Anon function, return type is not checked
aya> {->::num, "A"} ~
"A"

.# Named function, return type is checked
aya> {->::num, "A"}:f
{-> ::num , "A"}
aya> f
Expected type num, received: "A"
Function call traceback:
```

Note that argument types are always checked:

```
aya> 1 {a::str, } ~
Type error at argument: a::str
        Expected type: str
        Received: 1
```

## Argument and Return Type Names

Return types may be given an optional name. The name is for documentation only, it has no effect on the runtime

```
{a::list b::num -> value_out::num,
    ...
}
```

Note that the same parsing rule is used for arguments so they may be provided with or without names as well. By default a missing name will be changed to the name `_`. 

```
aya> {::num, _ 2 *} :double
{_::num, _ 2 *}
aya> 3 double
6
```

Practically speaking, since argument names must be unique, only one missing name is permitted


```
aya> {::num ::str, } 
Syntax Error: Duplicate variable name
{::num ::str, } 
~~~~~~~~~~~^
```


## Built in types

The following built-in types are available:

```
NAME   : EXAMPLES
num    : 4 3.14 :1i0
char   : 'a '\n'
str    : "hello"
list   : [ 1 2 3 ]
dict   : :{ 1:a 2 :b }
sym    : ::symbol ::x
block  : {1 1 +}
type   : num char list
any    : (matches any input)
union  : (union types, see below)
object : (user object created with struct or class) 
```


## Container Types

`list` and `dict` are containers. A container type can be specified:

```
aya> {l::[num]list, l {+}% lE /}:mean
{l::[num]list,l {+} % l E /}
aya> [1 2 3] mean
2
aya> ["a" "b" "c"] mean
Type error at argument: l::[num]list
        Expected type: [num]listT
        Received: [ "a" "b" "c" ]
```

For `dict`, the type specification applies to the values only:


```
aya> {d::[str]dict, d :V :# {:P}; }:print_values
{d::[str]dict,d :V {:P} :# ;}
aya> :{ "dog":a "cat":b } print_values
cat
dog
aya> :{ "dog":a "cat":b 3:c } print_values
Type error at argument: d::[str]dict
        Expected type: [str]dictT
        Received: :{
  3:c;
  "cat":b;
  "dog":a;
}
```

Type specifications can be nested:

```
aya> {l::[[num]list]list, l}:f
{l::[[num]list]list,l}
aya> [[1 2][3 4]] f
[
  [ 1 2 ]
  [ 3 4 ]
]
aya> [[1 2][3 "four"]] f
Type error at argument: l::[[num]list]list
        Expected type: [[num]listT]listT
        Received: [
  [ 1 2 ]
  [ 3 "four" ]
]
```

## Union Types

Union is a special type that matches with any of its inner types. Any number of types can be specified inside a union

```
aya> {x::[list num]union, x}:list_or_num
{x::[listnum]union,x}
aya> 1 list_or_num
1
aya> [1 2] list_or_num
[ 1 2 ]
aya> ::foo list_or_num
Type error at argument: x::[listnum]union
        Expected type: [list num]unionT
        Received: ::foo
```

## Types are objects

Types are plain aya objects that inherit from the `type` object. 

```
aya> list
list
aya> list :K
[ ::name ::__meta__ ]
aya> list.name
::list
```

They can be assigned and used like normal variables:

```
aya> list :t
list
aya> {x::t, x}:f
{x::t,x}
aya> [1 2] f
[ 1 2 ]
aya> 1 f
Type error at argument: x::t
        Expected type: list
        Received: 1
```

Dot syntax is supported:

```
aya> :{ list:l num:n }:my_module
:{
  list:l;
  num:n;
}
aya> {x::my_module.l y::my_module.n, x y}:f
{x::my_module.l y::my_module.n,x y}
aya> [1 2 3] 4 f
[ 1 2 3 ] 4
```

## Create container type `T`

Container types are just a list followed by a type. To create a single container type from them we use the `T` operator. They have the same syntax as block headers.


```
aya> [num]listT
[num]listT
aya> [1 2 3] [num]listT :@
1
aya> ["a" "b" "c"] [num]listT :@
0
```

## User types

User types are supported like any other type. Objects support inheritance. All user objects are a sub-type of `object`:


```
aya> struct point {x y}
point
aya> {p::point, p}:f
{p::point,p}
aya> 1 2 point! f
( 1 2 ) point!
aya> "not a point" f
Type error at argument: p::point
        Expected type: point
        Received: "not a point"
aya> {p::object, p}:f
{p::object,p}
aya> 1 2 point! f
( 1 2 ) point!
```

As mentioned above, this operator respects inheritance:


```
aya> class base
aya> ::derived base extend
derived
aya> derived! base :@
1
```

## User container types

By default, specifying an inner type for a struct or class is an error:

```
aya> struct pair {first second}
pair
aya> "a" "b" pair!
( a b ) pair!

.# No inner type specified, everything works okay
aya> {p::pair, p}:f
{p::pair,p}
aya> "a" "b" pair! f
( a b ) pair!

.# Redefine with an inner type specified
aya> {p::[str]pair, p}:f
{p::[str str]pair,p}

.# This results in an error no matter what the types are
aya> "a" "b" pair! f
Type error at argument: p::[str]pair
        Expected type: [str]pairT
        Received: ( a b ) pair!
```

Overload `__type_check__` to use inner types on structs or classes. `__type_check__` takes the value, the inner type, and the class as arguments. This function will only be called if the top level type already matches so `value` will always match the container type.

```
def pair::__type_check__ {value inner self,
    .# value is guaranteed to be a pair, we only
    .# need to check the inner types here
    value.first  inner.[0] :@
    value.second inner.[1] :@
    &
}
```

Then we can use it as expected

```
aya> 1 "cat" pair! {p::[num str]pair, "ok!":P}~
ok!
```

Any custom logic can be implemented inside of `__type_check__`. For example, here is an implementation of an **optional** type that only checks the value type if the value is valid

```

struct optional {value valid} ;

def optional::__type_check__ {value inner self,
    value.valid {
        value.value inner.[0] :@
    } {
        .# if empty, we don't care what the value is
        1
    } .?
}
```

Which can be used like so. (`:!` is the assert operator)

```
"value" 1 optional! optional :@ 1 :!
"value" 1 optional! [str]optionalT :@ 1 :!
"value" 0 optional! [str]optionalT :@ 1 :!
.# invalid, we don't care what the inner type is
::value 0 optional! [str]optionalT :@ 1 :!
.# valid, we DO care what the inner type is
::value 1 optional! [str]optionalT :@ 0 :!
```

## Performance

Runtime type checking is helpful for development and debugging but comes at a small performance cost. For high performance situations, disable the type checker using the `-r` CLI flag. The syntax for return types will remain valid but will be ignored by the parser.

**Warning:** When the `-r` flag is enabled, the type information is completely stripped. Be aware when writing code that may read type information from blocks that it will not exist in `-r` mode.

```
$ aya -ri
aya> {a::str b::num -> c::list, }
{a b,}
```
