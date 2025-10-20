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

Many operators can be overloaded for user types. Type `\? overloadable` in the repl for a full list. Many of the standard libraries use this feature to seamlessly integrate with the base library. 

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

Usage

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

Below is an example of a 2d vector "class" definition written *from scratch* without using any convience functions. Member functions and overloads work the same as they do for normal classes. The only major difference is object creation (`__new__` vs `__init__`) and the special variables `__pushself__` and `__type__` at the top of the metatable.

```
:{

  1:__pushself__;
  ::vec:__type__;

  .# Constructor
  {x y cls,
    :{
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


### Special Metatable Variables

```
  1:__pushself__;
  ::vec:__type__;
```

`__pushself__` tells aya to push a reference of the object to the stack when calling functions on it. It effectively enables the use of `self`

The symbol assigned to `__type__` is used for type checking and overloading the `:T` (get type) and `:@` (is instance) operators.

### Constructor

```
{x y cls,
    :{
        x:x;
        y:y;
        cls:__meta__;
    }
}:__new__;
```

Object construction with the `!` operator is just a standard operator overload that calls `__new__`. 

Note: For classes, `__new__` creates an instance of the object (i.e. `self`) and then calls `__init__` wich takes self as an argument.