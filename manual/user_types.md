# User-Defined Types

## Classes

Classes are defined using the `class` keyword

```
aya> class person
aya> person
<type 'person'> 
```

### Constructor

The constructor (`__init__`) takes any number of optional arguments followed by a `self` argument. `self` must always be the last argument in the list:

```
def person::__init__ {name age self,
    name self.:name;
    age  self.:age;
}
```

Create an object with the `!` operator:

```
aya> "Jane" 25 person! :jane;
(person 0x259984df)
aya> jane.name
"Jane" 
aya> jane.age
25
```

### Functions

Like the constructor, a member function takes `self` as an argument:

```
def person::greet {self,
    "Hi it's $(self.name)"
}
```

It is called like any other class variable:

```
aya> jane.greet
"Hi it's Jane" 
```

### Print/String Overloading

`__repr__` is a special function that is called when the object is printed. Overload it to change how an object is printed to the console

```
def person::__repr__ {self,
    "person: $(self.name)"
}
```

For example

```
aya> jane
person: Jane 
```

`__str__` is a special function that is called when the object is converted to a string

```
def person::__str__ {self,
    self.name
}
```

For example

```
aya> jane P
"Jane"
aya> "I saw $jane the other day"
"I saw Jane the other day"
```

### Operator Overloading

Many operators can be overloaded for user types. Type `\? overloadable` in the repl for a full list. Many of the standard libraries use this feature to seamlessly integrate with the base library. For example, the matrix library uses it for all math operators:

```
aya> import ::matrix
aya> [[1 2][3 4]] matrix! :m
[[ 1 2 ]
 [ 3 4 ]] 
aya> m 10 + 2 /
[[ 5.5 6 ]
 [ 6.5 7 ]] 
```

It is especially useful when writing libraries for code golf. The `asciiart` library uses it to create specialized operators on it's custom string type. Here is a 13 character function for creating a size `N` serpinski triangle:

```
aya> 4 "##`#"_\L{I}/
asciiart:
################
# # # # # # # # 
##  ##  ##  ##  
#   #   #   #   
####    ####    
# #     # #     
##      ##      
#       #       
########        
# # # #         
##  ##          
#   #           
####            
# #             
##              
# 
```

Let's overload the increment operator (`B`) to increment a person's age.

Here we modify the object directly

```
def person::__inc__ {self,
    self.age B self.:age;
}
```

Gives us

```
aya> jane.age
25
aya> jane B
aya> jane.age
```

If we don't want to modify the object but return a modified copy we could have chose to use the `$` syntax to pass a copy of the object instead:

```
def person::__inc__ {self$,
    self.age B self.:age;
    self .# Leave the copy on the stack
}

```
aya> jane.age
25
aya> jane B :jane_older;
aya> jane.age
25
aya> jane_older.age
26
```


### Class Variables & Functions

To define a shared class variable, assign it to the class directly:

```
def person::counter 0
```

or 

```
0 person.:counter;
```

We can then redefine our construtor to keep track of how many times we've called the constructor. 

Note that we can access `counter` directly from `self` but we need to use `__meta__` to update it to ensure we are updating the shared variable.

```
def person::__init__ {name age self,
    name self.:name;
    age  self.:age;
    self.counter 1+ self.__meta__.:counter;
}
```

Class functions take the class as an argument:


```
def person::create_anon {cls,
    "Anon" 20 cls!
}
```

They are called with the class (rather than with an instance)

```
aya> person.create_anon :anon
(person 0x7a1fe926)
aya> anon.name
"Anon"
```

### Inheritance

Aya classes support single inheritance. We can use the extend operator to create a class that is derived from another class. Here we create an `employee` class which extends the `person` class. It will simply add a `job` field.

Note that `extend` is not a keyword like `class` but an operator that takes the class as a symbol argument

```
::employee person extend;
```

or more generally

```
::derived base extend;
```

Our constructor calls the person constructor with `name` and `age` and then adds a `job` field.

```
def employee::__init__ {name age job self,
    .# call super constructor
    name age self super.__init__

    .# derived-specific code
    job self.:job;
}
```

In the example below, not that employee still calls `__repr__` we defined for the `person` class.

```
aya> "Bob" 30 "salesman" employee!
person: Bob
```

We can overload the `greet` function to include the job:

```
def employee::greet {self : greeting,
    .# call super greet
    .# must pass `self` to super
    self super.greet :greeting;

    .# append derived-specific greeting to output
    greeting ", I'm a $(self.job)" +
}
```

Calling it:

```
aya> bob.greet
"Hi it's Bob, I'm a Salesman"
```

## Structs

In Aya, structs are classes. The `struct` keyword simply creates a class with a few convience functions already defined.

The syntax is 

```
struct <name> {<member1>, <member2>, ...}
```

For example, lets create a `point` struct for representing a 2d point:

```
struct point {x y}
```

The constructor is created automatically for us. It takes each member as an argument in the same order they are defined

```
aya> 3 4 point! :p;
aya> p.x
3
aya> p.y
4
```

`__repr__` and `__str__` functions are also automatically created:

```
aya> p
( 3 4 ) point!
aya> p P
"( 3 4 ) point!"
```


## Internals

Keywords such as `class`, `struct`, and `def` are not actually keywords at all. They are regular aya functions defined completely in aya code (see base/__aya__.aya).

Classes, structs, and object instances are simply dictionaries with special __meta__ dictionaries. If you are interested in seeing how these are implemented entirely in aya, read on.

Below is an example of a 2d vector "class" definition written *from scratch* without using any convience functions. The subsections below will provide a breakdown of each section.

```
{,

  1:__pushself__;
  ::vec:__type__;

  .# Constructor
  {x y cls,
    {,
      x:x;
      y:y;
      cls:__meta__;
    }
  }:__new__;

  .# Member functions

  .# Print overload
  {self,
    "<$(self.x),$(self.y)>"
  }:__repr__;

  .# Compute vector length
  {self,
    self.x 2^ self.y 2^ + .^
  }:len;

  .# Operator overload
  {other self,
    other.x self.x +
    other.y self.y +
    vec!
  }:__add__;

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
