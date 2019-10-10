# Numbers
Aya has several representations for numbers: Num (represented by a double), BigNum, Rationals, and Complex (coming soon). Numbers are only promoted when needed. Number literals are always converted to Nums.
Aya uses standard mathematical operators.

```
aya> 3 4 +
7
aya> 5 6 -
-1
aya> 2 0.5 *
1
aya> 3 2 ^
9
aya> 6 4 /
1.5
aya> 6 3 /
2
```

`-` is never a unary operator.
```
aya> 8 3 -1
5 1
aya> -1
ERROR: Empty stack at operator '-'
stack:

just before:
	1
```

To write negative numbers, use a colon (with or without a -)

```
aya> :1.5
-1.5
aya> :-1.5
-1.5
```

# Special Number Literals

Special number literals always begin with a colon. Special number literals can be used to create negative numbers, **bignums**, **rationals**, and **complex numbers** *(coming soon)*.

```
.# Negative numbers
aya> :3
-3
aya> :-3
-3

.# Large numbers
aya> :123z
123
aya> :-3.1232z
-3.1232
aya> 9999999999999999999999
9223372036854775807
aya> :9999999999999999999999z
9999999999999999999999

.# Rationals
aya> :1r2
:1r2
aya> :3r
:3r1
```

Special number literals also provide ways for creating numbers using binary and hexadecimal formatting.

```
.# Hexadecimals begin with :0x
.# All letters must be lowercase
aya> :0xff
255
aya> :0x111
273

.# Binary literals begin with :0b
aya> :0b11010
26

.# Large hexidecimal and binary numbers are converted to BigNums
aya> :0xfffffffffffff
4503599627370495
aya> :0b11111111111111111111111111111111111111111111111111111111111111
4611686018427387903
```

# Misc. Number Literals

Like all number literals, these values are evaluated pre-runtime.

## Scientific Notation

Number literals of the form `:NeM` are evaluated to the literal number `N * 10^M`.

```
aya> :4e3
4000
aya> :2.45e12
2450000000000
aya> :1.1e-3
.0011
```

## PI Times

Number literals of the form `:NpM` are evaluated to the literal number `(N * PI)^M`. If no `M` is provided, use the value 1.

```
aya> :1p2
9.8696044
aya> :1p
3.14159265
aya> :3p2
88.82643961
```

## Root Constants

Number literals of the form `:NqM` are evaluated to the literal number `N^(1/M)`. The default value of M is 2.

```
aya> :2q
1.41421356
aya> :9q
3
aya> :27q3
3
```
