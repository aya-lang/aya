# Ticks
Sometimes we may need to traverse the stack backward before it has been evaluated. The tick (`) operator will move the item just after it back through the instructions 1 place. This will occur during runtime just BEFORE the item is evaluated.

```
aya> 1 `2 3 4
1 3 2 4
```
The object will be moved back BEFORE being evaluated

```
aya> 1 `+ 1  .# => 1 1 +
2
```

Ticks can be stacked. The object will move back one place for every tick.
```
aya> ``+ 3 4   .# => 3 4 +
7
```

Since ticks are evaluated at run time, users can define their own "infix" operators.

```
aya> {`*}:times;
aya> 3 times 4
12
```

# Groups

Objects on the stack can be grouped using parenthesis. Items in parenthesis are dumped and evaluated at run time. They are similar to a block followed by a `~`.

```
aya> {1 2 + 3}~ +
6
aya> (1 2 + 3) +
6
```

Grouped items are treated as one item by the interpreter and are therefore especially useful when used with the tick operator.

```
aya> `+ (1 2)
3
```

If a block is the only thing inside a group, it will be automatically dumped and evaluated.

```
aya> 1 2 {+}
1 2 {+}
aya> 1 2 ({+})
3
aya> 1 2 ({n m, n m +})   .# (This allows groups to have arguments)
3 
```