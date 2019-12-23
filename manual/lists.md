# Lists

List literals are created using square brackets and do not need commas. Literals are first evaluated as their own stack. The results remaining on the stack become the list items.

```
[1 2 3 4 5]      .# Do not use commas
[1 2 + 7 2 - 3!] .# => [3 5 -3]
```

List literals can grab items from the outer stack using the format `... [num| ...]` where num is an integer literal.

```
aya> 1 2 3 4 5 [3| 6 7 8]
1 2 [3 4 5 6 7 8]

aya> 'h 'e [2|'l 'l 'o]
"hello"

aya> "a" "b" [2|]
["a" "b"]
```

List grabbing only uses integer literals

```
aya> 2 :n
2
aya> 1 2 [n| 3 4]
ERROR: Empty stack at operator '|'
stack:
	1 2
just before:

```

## Essential List Operations


### List Indexing

Lists are indexed using square bracket syntax following a `.`. For Example:

```
aya> ["the" "cat" "in" "the" "hat"]:list
[ "the" "cat" "in" "the" "hat" ]
aya> list.[0]
"the"
```
Aya supports negative indexing, multiple indexing and filtering with this syntax.

```
aya> list.[:1]
"hat"
aya> list.[1 4]
[ "cat" "hat" ]
aya> list.[{E 3 =}]
[ "the" "cat" "the" "hat" ]
```

| Arg Type | Function                                               | Input              | Output  |
|----------|--------------------------------------------------------|--------------------|---------|
| Number   | Choose the nth item from the list (starting from 0)    | `[1 2 3].[1]`      | `2`     |
| List     | Use each item in the second list to index the first    | `"abc".[1 1 2]`    | `"bbc"` |
| Block    | Filter the list. Take all items that satisfy the block | `[1 1 2 2].[{1=}]` | `[1 1]` |

Filter examples:

```
aya> "Hello John Smith".[{.isupper}]
"HJS"
aya> 10R.[{2%}] .# {2%} : test if odd
[ 1 3 5 7 9 ]
```

Lists can also be indexed using the `I` or `.I` operators. The `.I` operator will leave both the list and the element on the stack.

```
aya> ["the" "cat" "in" "the" "hat"]:list
[ "the" "cat" "in" "the" "hat" ]
aya> list 0 I
"the"
aya> list :1 .I
[ "the" "cat" "in" "the" "hat" ] "hat"
```

### Modifying Lists

Use the following syntax to set elements of a list

```
item list.:[i]
```
which is equivalent to `list[i] = item` in C-style languages.


Extend (`K`)

```
aya> [1 2 3] :list
[ 1 2 3 ]
aya> list [4 5 6] K
[ 1 2 3 4 5 6 ]
aya> list
[ 1 2 3 4 5 6 ]
```

Reshape (`L`)

```
aya> 9R [3 3] L
[ [ 1 2 3 ] [ 4 5 6 ] [ 7 8 9 ] ]
aya> [1 2] [2 2 2] L
[ [ [ 1 2 ] [ 1 2 ] ] [ [ 1 2 ] [ 1 2 ] ] ]
aya> 100R [2 3] L
[ [ 1 2 3 ] [ 4 5 6 ] ]
```

Flatten (`.F`)

```
aya> [[1 2] [3] 4 [[5] 6]] .F
[ 1 2 3 4 5 6 ]
```

Pop from front / back

```
aya> [1 2 3] B
[ 1 2 ] 3
aya> [1 2 3] V
[ 2 3 ] 1
```

Append to front / back

```
aya> 1 [2 3] .B
[ 2 3 1 ]
aya> 1 [2 3] .V
[ 1 2 3 ]
```

### Generators

#### Range (`R`)

One item: create a range from `1` (or `'a'`) to that number.

```
10 R    .# => [1 2 3 4 5 6 7 8 9 10]
'd R    .# => "abcd"
```

Two items: create a range from the first to the second.

```
[5 10] R     .# => [5 6 7 8 9 10]
['z 'w] R    .# => "zyxw"
```

Three items: create a range from the first to the third using the second as a step.

```
[0 0.5 2] R      .# => [0 0.5 1.0 1.5 2.0]
```

## List comprehension

When commas are used inside of a list literal, the list is created using list comprehension. List comprehension follows the format `[range, map, filter1, filter2,  ..., filterK]`. The range section is evaluated like the `R` operator. When the list is evaluated, the sections are evaluated from left to right; first create the range, then map the block to the values, then apply the filters. All filters must be satisfied for an item to be added to the list.  

If the map section is left empty, the list is evaluated as a basic range.

```
aya> [10 ,]
[1 2 3 4 5 6 7 8 9 10]

aya> ['\U00A3' '\U00B0' ,]
"£¤¥¦§¨©ª«¬­®¯°"

aya> [0 3 15 , T]
[0 -3 -6 -9 -12 -15]
```

Here are some examples using map and filter.

```
aya> [10, 2*]
[2 4 6 8 10 12 14 16 18 20]

aya> [10, 2*, 5<]
[2 4]

aya> [10, 2*, 5<, 4=!]
[2]

.# Can grab from stack
aya> 10 [1|,]
[ 1 2 3 4 5 6 7 8 9 10 ]

aya> 3 [1| 6 18, 2*]
[ 6 12 18 24 30 36 ]
```

If a list literal is used as the first section of a list comprehension, the list comprehension is simply applied to the inner list.

```
aya> [1 2 3]:l; [l, 2*]
[ 2 4 6 ]

aya> [ [1 2 3 4 5], 2*, 7<]
[2 4 6]
```

If there are two or more lists used as the first argument of a list comprehension, and each list is the same length, all respective elements of each list will be added to the stack when applying the map and filter sections.

```
aya> [ [1 2 3][4 5 6], +]
[5 7 9]

aya> [ "hello" "world", K]
[ "hw" "eo" "lr" "ll" "od" ]
```

## The Broadcast Operator

`#` is a very powerful *infix* operator. It's primary function is map. It takes the arguments from its right side and maps them to the list on the left side.

```
[1 2 3] # {1 +} .# => [2 3 4]
```

If a block is not given on the right side, `#` will collect items until an operator or variable is encountered.

```
.# Same as the previous example
[1 2 3] # 1 + .# => [2 3 4]
```

`#` will also collect items on its left side until a list is hit. It will add these items to the front of the block being mapped to.

```
.# Also the same as the previous line
[1 2 3] 1 # + .# => [2 3 4]
```

This operator can be used to construct for loops on variables

```
"hello" :str;
str # {c,
    c.upper
}
=> "HELLO"
```

The `:#` operator works the same way except it always takes a list on the left and a block on the right. This can result in minor performance improvements if the complete operation is known ahead of time.

```
list :# {block}
```

```
aya> [1 2 3] :# {3+}
[ 4 5 6 ]

aya> [1 2 3] 3 :# +
ERROR: Empty stack at operator ':#'

aya> [1 2 3] 3 # +
[ 4 5 6 ]

aya> [1 2 3] 3 :# {+}
TYPE ERROR: Type error at (:#):
	Expected ((L:#B|D:#B))
	Received ({+} 3 )
stack:
	[ 1 2 3 ]
just before:

```
