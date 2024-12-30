# Dictionaries

A dictionary is a set of key-value pairs. The keys must always be valid variable names. A dictionary literal is created using a block with an empty header. The block is evaluated and all variables are in assigned in the scope of the dictionary.

```
:{
  <dictionary body>
}
```

Below is a simple dictionary example.

```
.# Define a simple dictionary
:{
  1:one;
  2:two;
  3:three;
}:numbers;
```

Empty dictionaries are created if the block and the header are empty.

```
aya> :{}
:{
}
```

## Accessing Values

Access variables are used to access variables in dictionaries and user types. To create an access variables, use a dot before the variable name.

```
aya> numbers.two
2

.# whitespace is optional
aya> numbers .one
1
```

## Assigning / Creating Values

Dictionary values can be assigned using the `.:` operator.

```
aya> :{ 1:a 2:b} :d
:{
  1:a;
  2:b;
}
aya> 4 d.:a
:{
  4:a;
  2:b;
}
aya> 9 d.:c
:{
  4:a;
  2:b;
  9:c;
}
```

They may also be assigned using the following syntax:

```
item dict.:[key]
```

where `key` is a string or a symbol.

For example:

```
aya> :{ 0:x } :dict;
aya> 1 dict.:[::y]
aya> dict
:{
  0:x;
  1:y;
}

aya> 1 dict.:["y"]
aya> dict
:{
  0:x;
  1:y;
}
```

Loop over k/v pairs in a dict using the `:#` operator

```
aya> dict :# {k v, v 1 + dict.:[k]}
aya> dict
:{
  1:x;
  2:y;
}
```

## Metatables

In Aya, metatables can be used to define custom types with separate functionality and moderate operator overloading. User types are represented internally as an array of objects paired with a dictionary.
Any dictionary can contain a read-only set of variables as a metatable. Metatables typically contain functions that act on the dictionaries values. For example, if we define the metatable

```
:{ {self, self.x self.y +}:sum;  {}:donothing; } :meta;
```

and the dictionary

```
:{ 1:x 2:y {}:none } :dict;
```

we can set the metatable using the MO operator like so

```
aya> meta meta.:__meta__
:{
  1:x;
  2:y;
  {}:none;
}
```

We can see that the dict still has the values `x` and `y` but it also now has a hidden entry for the key sum in its metatable. When we call the metatable variable, the dictionary will be left on the stack and the metatable value will be evaluated.

```
aya> dict.sum
3
aya> dict.donothing
:{
  1:x;
  2:y;
  {}:none;
}
```