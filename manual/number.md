# Numbers 
Aya has several representations for numbers: Num (represented by a double), BigNum, Rationals, and Complex (coming soon). Numbers are only promoted when needed. Number literals are always converted to Nums.
Aya uses standard mathematical operators.

```
3 4 +   .# => 7
5 6 -   .# => -1
2 0.5 * .# => 1.0
3 2 ^   .# => 9
6 4 /   .# => 1.5
6 2 /   .# => 3
```

`-` is never a unary operator. 
```
8 3 -1  .# is evaluated as (8 3-) 1 => 5 1
-1      .# ERROR: Empty stack at operator '-'
```

To write negative numbers, use a colon (with or without a -)

```
:1.5   .# => -1.5
:-1.5  .# => -1.5
```

## Special Number Literals

*See [Syntax Overview: Numbers](./syntax_overview#numbers)*

Special number literals always begin with a colon. Special number literals can be used to create negative numbers, **bignums**, **rationals**, and **complex numbers** *(coming soon)*. 

```
.# A colon paired with a number with no additional formatting is negative
:3    .# -3
:-3   .# -3

.# BigNums end with a z
:123z      .# 123
:-3.1232z  .# -3.1232

.# Rational numbers separated numerator and denominator with a r
:1r2   .# 1/2
:3r    .# 3/1

.# Complex numbers are separated with an i
:1i    .# The imaginary unit
:2i5   .# 2i + 5
```

Special number literals also provide ways for creating numbers using binary and hexadecimal formatting.

```
.# Hexadecimals begin with :0x
.# All letters must be lowercase
:0xff  .# 255
:0x111 .# 273

.# Binary literals begin with :0b
:0b11010  .# 26

.# Large hexidecimal and binary numbers are converted to BigNums
:0xfffffff    .# 268435455 (Num)
:0xffffffff   .# 4294967295 (BigNum)
```

## Misc. Number Literals

Like all number literals, these values are evaluated pre-runtime. 

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