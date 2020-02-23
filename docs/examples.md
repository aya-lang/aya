#Aya Code Examples

## !!NOTE!! These examples are outdated, for up-to-date examples, see the examples folder



##Print "2014" using no numbers in source  
```
cEb*d{B}cB%+
```

##Recursive Greatest Common Divisor
```
{aN bN,
  [
    {0} a0=b0=&.?     .# Both numbers are 0
    {aMA} b0=.?       .# b is zero, return abs(a)
    {bMA aMAbMA% gcd} .# b is not zero, compute gcd recursively
  ].c
}:gcd;
```

##Prints a train of 3 carts (can print any positive int of carts)
```
[" ______  ""|      | "" ()--() ~"]#{3LSB'\n'+}S.P
```

##Requests the name of a C source file and returns the number of lines of code
```
"".qG"/\\*[^/]+\\/|[\\n|\\t+|\\s+]//.+"|S".+"&{_E\"[\\t|\\s]"&E=!}IE
```

##Project Euler 1  
```
999R{15.+=!}IS
```

##Project Euler 8 (where `list` is the sequence of numbers)  
```
list 13R{_@_@I{*}.F\@#B}987%;;.Amax
```

##Project Euler 34  
```
[3 5E,,$P:'48-M!S=]S
```

##Project Euler 48  
```
kR#{_^}SdE%
```

##Generate the nth Fibonacci number  
```
0\1{_@+}@~;
```

## Plot a sin wave using its Taylor Series

```
[0 0.001 pi2*, {x, x x3^3M!/- x5^5M!/+ x7^7M!/- x9^9M!/+ x11^11M!/- x13^13M!/+ x15^15M!/- x17^17M!/+ x19^19M!/-} ] .x
```

##Pills
```
[10,['\in''\ni']\2%!I]
```

##Fib w/ Variables
Ten thousand variable lookups and definitions

```
1:a; 10000 0\1{a@+:a}@%; ;
```

100 thousand lookups:
```
{1:a; {a 1+ :a;} 100000 % a;}.time
31 
```

##Repeat with delim
repeats a string with a delimiter in between

```
    .#Inline Version:
    L{d,{d+}#}~S.(

    {strS delimC countI,
        str count L{delim +}#S.)}
    :repeatwdelim ;
```

##Cool Sin wave

```
    [0 0.001 pi2*, Ms.Q15^*].p
```

##Random Card

```
    ["KQJT98765432A"13Q#"HCSD"4Q#];
    [
        ["King""Queen""Jack""Ten"'9'8'7'6'5'4'3'2'1]13Q#" of "
        ["Hearts""Diamonds""Clubs""Spades"]4Q#
    ]S;
```

##Swap two items in a list

```
    {list i j,
        list.[i] :tmp;
        list.[j] list.:[i]
        tmp list.:[j]
    }:swap;
```

##Shuffle
shuffles a list using the swap function

```
    {list,
        {list _E_Q\Q swap : list;}5% list
    }:shuffle;
```

##Find words starting with...
Finds all words that start with the letter c in the string

```
    {c,
        ["\b[" c "|" c! "]\w+"]S &
    }
```
