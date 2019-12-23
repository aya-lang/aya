# Numbers

Aya has several built in ways of representing numbers including standard floats, arbitrary precision floats and fractional numbers. All standard floats are represented internally as doubles.

## Number Types

There are three types of numbers in Aya. They may be used interchangeably with most operators. All numbers have the type `::num`

  - Standard
  - Big
  - Fractional

### Standard Numbers

Standard numbers are represented internally as a double.

```
aya> 3.14
3.14
aya> 1.0
1
aya> 1
1
```

Since the `-` character is reserved for the subtraction operator, negative numbers are written with a leading `:` or `:-`.

```
aya> :3
-3
aya> :-3
-3
aya> -1
ERROR: Unexpected empty stack while executing instruction: -
stack:

just before:
        1
aya> 10 4 -2
6 2
```

Unless explicitly stated (see *Big Numbers*), all number literals are parsed as doubles

```
aya> 1000000000000000000000000000000
9223372036854775807
```

Numbers may still be treated like signed integers by many operators.

```
.# Bitshift left
aya> 9 1 .(
18

.# Bitshift right
aya> 9 1 .)
4

.# Bitwise not
aya> 9 C
-10
```

### Big Numbers

Big numbers are represented internally using the `ApFloat` library. They may be of arbitrary size and have arbitrary fractional precision.

They have the form `:Nz` where `N` is a numeric literal which may or may not include a leading `-`

```
aya> :123z
123
aya> :-3.1232z
-3.1232
aya> 9999999999999999999999
9223372036854775807
aya> :9999999999999999999999z
9999999999999999999999
```

### Fractional numbers

Fraction numbers are represented internally as a pair of `long`s.

They have the form `:NrM` where `N` and `M` are numeric literals which may or may not include a leading `-`

```
aya> :1r2
:1r2
aya> :3r
:3r1
aya> 3.4 Mr
:17r5
```

All standard math functions work as expected. Fractionals take precedence over standard numbers for most operations.

```
aya> :1r2 3 *
:3r2
aya> :1r2 3.1 *
:31r20
aya> :1r2 :1r4 +
:3r4
```

## Number Syntax

Special number literals always begin with a colon. We have already seen negative numbers and big numbers which are a specific cases of the special number format.


### Hexadecimal and Binary Literals

Special number literals also provide ways for creating numbers using binary and hexadecimal formatting. Unlike the number types above, these are simply syntax for representing existing number types

Hexadecimals begin with `:0x`. All letters must be lowercase

```
aya> :0xff
255
aya> :0x111
273
```

Binary literals begin with :0b

```
aya> :0b11010
26
```

Large hexadecimal and binary numbers are  automatically converted to big numbers

```
aya> :0xfffffffffffff
4503599627370495
aya> :0b11111111111111111111111111111111111111111111111111111111111111
4611686018427387903
```

### Scientific Notation

Number literals of the form `:NeM` are evaluated to the literal number `N * 10^M`.

```
aya> :4e3
4000
aya> :2.45e12
2450000000000
aya> :1.1e-3
.0011
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
