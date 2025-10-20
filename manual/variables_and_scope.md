# Variables

*See [Syntax Overview: Variables](./syntax_overview.md#variables)*  

Variables may only contain lower case letters and underscores. They are assigned using the colon (:) operator. The value is left on the stack after the assignment has occurred.

```
aya> 1 :a
1
aya> 3:b a +
4
```

For single-character variables, you may also use Unicode characters, these identify variables based on their UTF-16 code-units.  
Single-character variables do not need to be separated by spaces.  
Characters whose code-points are in range `[0x0, 0xffff]` are encoded using a single code-unit and refer to their literal character.  
Those in range `[0x10000, 0x10ffff]` are encoded using two code-units refer to pairs of variables.

These pairs of code-units are called "surrogate pairs", they split the Unicode code-point into 10 bits each.  
Consequently, code-points in range `[0x0, 0x3ff]` can be referred to by these pairs.

```
aya> 1 2 :á; :ḃ; áḃ
2 1
aya> 3:c; 4:d;
aya> 𨱤 .# '𨱤' encodes 'c' in the first pair, and 'd' in the second.
3 4
aya> 𨱤񈑤 .# '񈑤' encodes 'á' in the first pair, and 'd' in the second.
3 4 2 4
```

## Variable Scope

A new scope is introduced if a block contains any variable declaration in its header. When a variable assignment occurs, the interpreter will walk outward until a reference to that variable appears. If it does not appear in any of the scopes before the global scope, a new reference will be created there. In order to ensure a variable is using local scope, the variable name must be included in the block header. If a block does not contain a header, a new scope will not be introduced. These concepts are best demonstrated by example.

Let us introduce the variables a and b:

```
"A":a; "B":b;
```

When blocks have arguments, a scope is introduced for that variable. Here, the number zero is assigned to `b` within the scope of the block. When the block ends, the scope is destroyed and we reference the now global variable `b`.

```
aya> 0 {b, b.P}~ b.P
0B
```

Local variables also create local scopes for that variable. Here, we create a local scope for the variable `b`. `a` is not included in the new scope.

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
