# Variables

Variables may only contain 12 or less lower case letters. They are assigned using the colon (:) operator. The value is left on the stack after the assignment has occurred.

```
aya> 1 :a
1
aya> 3:b a +
4
```

Variables may only be 12 characters long. If a variable is more than 12 characters long, the parser will print a warning and only use the first 12 characters of the variable. This means that two seemingly different variable names that share the same first 12 characters will be considered the same variable.

```
thisisaverylongvariablename
|---used---|--ignored-->
```

If the first 12 characters are the same, the rest is ignored. The parser evaluates them as the same variable:

```
aya> "hello":abcdefghijklmnop;
aya> abcdefghijklmnop
"hello"

.# The last 4 letters of this var are different
aya> abcdefghijklwxyz
"hello"
```

## Special Character Variables

Any of the named special characters can be used as a variable. Since variables can only contain lowercase letters, the parser translates the special character into its name and uses the name as the variable internally. This means that internally, the character and the name of the character are the same variable. Unicode characters that do not have a defined name cannot be used as variables. Variables congaing special characters may only be one symbol long.

```
aya> 0.05:α;
aya> 100 α *
5.0

.# the number 1 is only assigned to β
aya> 1:βα
1 0.05

aya> αβ
0.05 1
```

Internally, character names are the same variable as the character.

```
aya> 2:alpha;
aya> 4:β;
aya> α beta *
8
```

# Variable Scope

A new scope is introduced if a block contains any variable declaration in its header. When a variable assignment occurs, the interpreter will walk outward until a reference to that variable appears. If it does not appear in any of the scopes before the global scope, a new reference will be created there. In order to ensure a variable is using local scope, the variable name must be included in the block header. If a block does not contain a header, a new scope will not be introduced. These concepts are best demonstrated by example.

Let us introduce the variables a and b:

```
"A":a; "B":b;
```

When blocks have arguments, a scope is introduced for that variable. Here, the number zero is assigned to b within the scope of the block. When the block ends, the scope is destroyed and we reference the now global variable b.

```
aya> 0 {b, b.P}~ b.P
0B
```

Local variables also create local scopes for that variable. Here, we create a local scope for the variable b. a is not included in the new scope.

```
aya> .# Local variable b declared in header
  {:b,
    0:a;
    1:b;
    "a = $a," .P
    "b = $b\n" .P
  }~ 
  "a = $a," .P
  "a = $b\n" .P

a = 0,b = 1
a = 0,a = B
```