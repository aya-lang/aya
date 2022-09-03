# Debugging

Aya has built-in support for setting breakpoints using the `bp` command. For example:

```
{a b : c,
    a b + :c;
    [a b]
    bp
    c J
}:fn;
```

Calling this function with pause execution at the location of `bp` and open a shell for inspection.

```
aya> 1 2 fn
Execution paused, enter '.' to continue
Stack: [ 1 2 ] 
Next instructions:   c J

aya (debug)> a
1 

aya (debug)> c
3 

aya (debug)> .
[ 1 2 3 ] 
```

Setting `__aya__.ignore_breakpoints` to `1` will disable breakpoints in the session and setting it to `0` will enable them. It is set to `0` by default.

```
aya> 1 __aya__.:ignore_breakpoints;

aya> 1 2 fn
[ 1 2 3 ] 

aya> 0 __aya__.:ignore_breakpoints;

aya> 1 2 fn
Execution paused, enter '.' to continue
...
```