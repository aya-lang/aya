# 5. Lists

  - 2. Control
    - if, if/else, elseif
    - counted loop
    - for loop
    - while loop
    - exceptions

## The Stack

Aya is a stack based language. Code is first parsed into a list of instructions and then executed from left to right. When execution is finished, the state of the stack is dumped to stdout. All instructions are either **data** or an **operation**. All **data** is simply moved from the instructions to the top of the stack. **Operations** perform some operation on the state of the stack which *may or may not* consume data from the stack and *may or may not* push a result to the stack.

```
aya> 1 4 + 3 -
2
```

Below are the steps by which the above was evaluated:

| Stack | Instructions | Note |
|---|---|---|
| | `1 4 + 3 -` | Initially the stack is empty |
| `1` | `4 + 3 -` | `1` is **data** so it is simply moved from the instructions to the stack |
| `1 4` | `+ 3 -` | `4` is also **data** so it is pushed to the top if the stack |
| `5` | `3 -` | `+` is an **operation** with two operands. It pops `1` and `4` from the stack and adds them. The result, `5`, is pushed back to the stack |
| `5 3` | `-` | `3` is **data**. Push to stack |
| `2` | | `-` is an **operation**. It pops its right operand before its left operand so that the expression `x y -` evaluates to `x-y` |

## Built-in data types

Aya has many several built-in data types such as numbers, strings, lists, dictionaries, symbols, and expressions (called *blocks*). For now we will look at just a few.

### Numbers

There are several ways to create numbers (see Numbers). Basic numbers are written as you would expect:

```
aya> 1
1
aya> 12.5
12.5
```

Negative numbers are written with a `:` instead of a `-`:

```
aya> :4
-4
aya> :3.5 2 +
-1.5
```

### Strings

Strings are written using double quotes. They may escapes using a backslash.

```
aya> "Hello world!"
"Hello world!"
aya> "A\tB\nC"
"A      B
C"
```

### Lists

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

## Operations

There are a large number of **operation** instructions supported by Aya including operators, list comprehension, variable access and assignment, string comprehension, and macros. For now we will just look at built in operators.

### Operator Syntax

There are two types of operators: single character (such as `+` or `-`), and double character (such as `.!` or `Ms`).

Most ascii symbols and all upper case letters (except `M`) are single character operators. Below are a few examples:

| Operator | Execution result |
|---|---|
| `+`, `-`, `*`, `/` | Pop two numbers and add, subtract, multiply, or divide them respectively |
| `P` | Pop a single value, convert to string and push back to stack |
| `R` | Pop a value from the stack `N` and create a list `[1..N]` |
| `$` | Peek (leave the value on the stack) the value from the top of the stack, push a deepcopy back to the stack |

Examples:
