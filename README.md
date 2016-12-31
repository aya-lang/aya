# The Aya Programming Language

![Running Aya from the command line. ](images/qsearch.png)

## Features

  - Terse, yet readable syntax
  - Fully loaded with a complete standard library
  - Modules with namespace like functionality
  - Basic support for objects and data structures
  - Macro-like pre-evaluation stack manipulation
  - Functional feel: List comprehension etc.
  - String Interpolation, Unicode, and special characters
  - Comes pre-packaged with a feature packed GUI
  - Built in plotting
  - Interactive help and Documentation
  - Colored printing and simple UI elements

## Overview

Aya is a stack based programming language originally intended for code golf and programming puzzles. However, the language is very different from most golfing languages. Its support for user-defined types and macro-like function definitions allow for complex programs and data structures. It excels in cases were programs need to be written quickly.

Aya comes fully-loaded with a standard library written entirely in Aya code. The standard library features types such as fractions, dictionaries, matrices, stacks, and more. It also features hundreds of functions for working working on numerical computations, strings, plotting and file I/O.

Aya also features a minimal GUI for easily writing code and working using the Aya language. The GUI features colored console printing, plotting, tab-completion for special characters, and most importantly, an interactive way to search QuickSearch help data.

## Usage

Aya requires Java 8. To run the GUI, run the command:

```
java -jar aya.jar
```

To run Aya interactively from the command line use `-i`

```
java -jar aya.jar -i
```

To run files from the command line, use `-f`

```
java -jar aya.jar -f file.aya
```

## Examples

### Golfed Project Euler Problem 6

```
hR_S2^\2^S-

Explanation:
hR            Generate a list [1,2..100]
  _           Duplicate the list
   S2^        Evaluate the sum of the list and square it
      \       Bring the other list to the top of the stack
       2#^    Square each element in the list
          S-  Subtract the sum of this list from the previous sum
```

### Recursive factorial function written in the style of C.

The tick (\`) operator is used to convert postfix operators into infix ones.

```
`:factorial {n,
  if (n `.< 1) {
    1
  } {
    n `* ( `factorial(n`-1) )
  }
}
```

### Define a 2D vector type

Type definition:

```
{,

  .# Constructor
  {x y, {, x:x y:y}vec MO}:new;

  .# Print Override
  {self, "<$(self.x),$(self.y)>"}:repr;

  .# Member Function
  {self, self.x2^ self.y2^ + Mq}:len;

  .# Operator Overload
  {a b, [a.x b.x+ a.y b.y+] vec MO}:plus

}:vec;

```

Call constructor using `!` operator and print using `.show` definition:

```
aya> 1 2 vec!
<1,2>

```
Perform operations on the type:

```
aya> 3 4 vec! :v
<3,4>

aya> v.len
5.0

aya> 10 10 vec! v +
<13,14>
```

### Plot a sine series from 0 to 4pi using the built in plotting tool.

```
.# Create a plot instance
plot!:plt;

.# Set the domain of the plot
[0dy4pi*]R plt.domain

.# Add a series to the plot
for 'n (4R) {
  nP 1 [] {x,[nR [nR,x*Ms],*]S} plt.addexpr
};

.# Set the title of the plot
"Demo: Sine Series" plt.:title

.# Set the stroke
2 plt.:stroke

.# X axis range
[0 pi4*] plt.:xaxis

.# Open a plot window
plt.view
```

Output:

![Running aya from the command line. ](images/sinsrs.png)

# TODO

  - **Optimization**: Parts of the interpreter run fairly slow and can be optimized to run faster. Many operators also need to be optimized.
  - **More Operators**: Most of the dot (`.<op>`) and misc (`M<op>`) operators have not yet been assigned.
  - **Refine the Standard Library**: Debug, fix small errors, clean
  - **Better I/O Support**: Currently IO includes text files (input and output) and the ability to download files from the web. An official IOStream object should be implemented.
  - **Improved Plotting**: More parameters and customization
