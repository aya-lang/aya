# Dictionaries

A dictionary is a set of key-value pairs. The keys must always be valid variable names. A dictionary literal is created using a block with an empty header. The block is evaluated and all variables are in assigned in the scope of the dictionary.

```
{,
  <dictionary body>
}
```

Below is a simple dictionary example.

```
.# Define a simple dictionary
{,
  1:one;
  2:two;
  3:three;
}:numbers;
```

Empty dictionaries are created if the block and the header are empty.

```
aya> {,}
{,
} 
```

## Accessing Values

Access variables are used to access variables in dictionaries and user types. To create an access variables, use a dot before the variable name.

```
aya> numbers .one
1 

.# whitespace is optional
aya> numbers.two
2
```

## Assigning / Creating Values 

Dictionary values can be assigned using the `.:` operator. 

```
aya> {, 1:a 2:b} :d
{,
  1:a;
  2:b;
} 
aya> 4 d.:a
{,
  4:a;
  2:b;
} 
aya> 9 d.:c
{,
  4:a;
  2:b;
  9:c;
} 
```

They may also be dynamically assigned using the following syntax:

```
item dict.:[key]
```

where `key` is a `Symbol`.

For example:

```
aya> {, 0:x } :dict;
aya> 1 dict.:[::y]
aya> dict
{,
  0:x;
  1:y;
} 

aya> dict :# {k v, v 1 + dict.:[k]}
aya> dict
{,
  1:x;
  2:y;
} 
```

# Metatables

In Aya, metatables can be used to define custom types with separate functionality and moderate operator overloading. User types are represented internally as an array of objects paired with a dictionary. 
Any dictionary can contain a read-only set of variables as a metatable. Metatables typically contain functions that act on the dictionaries values. For example, if we define the metatable

```
{, {self, self.x self.y +}:sum;  {}:donothing; } :meta;
```

and the dictionary

```
{, 1:x 2:y {}:none } :dict;
```

we can set the metatable using the MO operator like so

```
aya> dict meta MO
{,
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
{,
  1:x;
  2:y;
  {}:none;
} 
```

We can still add and access dictionary values normally. If we overwrite a metatable key, it will override the value locally in the dictionary, not the entire metatable.

```
aya> {, 1:x 2:y} meta MO :a
{,
  1:x;
  2:y;
} 
aya> {, 3:x 4:y} meta MO :b
{,
  3:x;
  4:y;
} 
aya> {;"Something!":P} a.:donothing
{,
  1:x;
  2:y;
  {; "Something!" :P}:donothing;
} 
aya> a.donothing
Something!
aya> b.donothing
{,
  3:x;
  4:y;
} 
```

# User-Defined Types

Using metatables, dictionaries, and overloading we can define our own types (or "classes"). Here we will first define a simple 2D vector type and then walk through each of the import steps involved in making it.

```
{,
  .# Constructor
  {xN yN,
    {, x:x y:y} vec MO
  }:new;

  .# Member functions

  {self, 
    "<$(self.x),$(self.y)>"
  }:repr:str;

  {self,
    self.x 2^ self.y 2^ + Mq
  }:length;

  .# Operator Overload (+)
  {a b,
    {, a.x b.x+:x  a.y b.y+:y } vec MO
  }:add;

}:vec;
```

## Object Creation

In order to create a an instance of a user type, we use the MO operator to assign a metatable to a new dictionary. To create a vector object, we create a dictionary containing the default values for `x` and `y` and then assign `vec` as its metatable.

```
.# Create a vec object
{, 0:x 0:y} vec MO
```

This syntax can be a bit repetitive. In order to address this issue, we introduce *constructors*.

### Constructor

If there exists a function new in the metatable definition, it will be used as the constructor for the object. The constructor can be called in the following ways:

```
.# Calling the .new function manually
aya> 3 6 vec.new
<3,6>

.# Using the ! operator after the name of the dictionary
aya> 1.1 3 vec!
<1.1,3>
```
Notice that when the object is printed to the console, it prints using our definition of `.repr`. Aya will automatically use `.repr` and `.str` to convert objects to strings whenever necessary ( e.g. printing to the console, calling the `P` *(cast to string)* operator, etc.). This is discussed in the next section.

## String Conversion

If there exists a function `str` defined for a given user type, Aya will call it whenever the type is converted into a string. If there exists a function `repr` defined for a given user type, Aya will use it whenever it prints the object to the console. Aya expects a string to be returned from these functions but does not check before converting. If they do not return a string, unexpected results may occur. In the `vec` example, we defined a `repr` and `str` function and we can see the result every time the `vec` is printed to the console.

```
aya> 1 2 vec!   .# Uses .repr
<1,2>

aya> 1 2 vec! P  .# Uses .str
"<1,2>"
```

## Operator Overloading

Several operators have the capability to be overloaded be defining functions with special names. For example, the function `add` will be called if the user calls `+` on a user object The following operators may be overloaded:

```
+ - * / & | $ % P Q
```

These operators and their function names can be found by searching "overloadable" in the QuickSearch feature of aya.

In our vec example, we defined the following function:

```
  {a b,
    [a.x b.x+ a.y b.y+] vect MO
  }:add;
```

Now the following statements are equivalent:

```
aya> 1 2 vec!  3 4 vec!.plus
<4,6>
aya> 1 2 vec!  3 4 vec! +
<4,6>
```

**NOTE**: The number of arguments used in an overloaded function be greater than or equal to the number of arguments the operator normally takes. For example, the + operator must take at least two arguments and the $ operator must take at least 1.

For more examples on using dictionaries and metatables as user types, see the standard library files for `matrix`, `color`, and `date`.