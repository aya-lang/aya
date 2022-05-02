# Characters

Character literals are created using single quotes. Most characters do not need closing quotes.

```
'a     .# => 'a
'p'q   .# => 'p 'q
```

## Special Characters

Using a `\` after a single quote denotes a special character. Special characters always need a closing single quote.

### Hex character literals

Hex literal characters are written using a `'\x___'` and need closing quotes.

```
aya> '\xFF'
'ÿ'
aya> '\x00A1'
'¡'
```

Leading zeros and case do not matter.

```
aya> '\x000000fF'
'ÿ
```


### Named Characters

Many characters have names. All names consist only of lowercase alphabetical characters. Named characters can be used like so within Aya:

```
'\alpha'     .# => 'α'
'\pi'        .# => 'π'
'\because'   .# => '∵'
'\n'         .# => <newline>
'\t'         .# => <tab>
```

To add or override a named character from within Aya, use the `Mk` operator.

```
aya> '\integral'
SYNTAX ERROR: '\integral' is not a valid special character

aya> '\x222b' "integral" Mk

aya> '\integral'
'∫'
```

# Strings

Strings are created using the double quote character `"`.

```
"I am a string"
"I am a string containing a newline character\n\t and a tab."
```

Strings may span multiple lines.

```
"I am a string containing a newline character
	and a tab."
```

Strings can contain special characters using `\{___}`. Brackets can contain named characters or Unicode literals.

```
"Jack \{heart}s Jill"         .# => "Jack ♥s Jill"
"sin(\{theta}) = \{alpha}"    .# => "sin(θ) = α"
"\{x00BF}Que tal?"            .# => "¿Que tal?"
```

Many operators treat a string as a list of characters.

```
"Hello " "world!" K  .# => "Hello world!"
['s't'r'i'n'g]       .# => "string"
"abcde".[2]          .# => 'c'
```

## String Interpolation

Use the `$` character within a string to evaluate the variable or statement following it.
If used with a variable name, evaluate the variable name.

```
aya> 5:num;
aya> "I have $num apples"
"I have 5 apples"
```

If used with a group `()`, evaluate the group.

```
aya> "I have $(1 num +) bananas"
"I have 6 bananas"
```

If there are more than one item left on the stack, aya dumps the stack inside square brackets.

```
aya> 123:playera;
aya> 116:playerb;
aya> "The final scores are $(playera playerb)!"
"The final scores are [ 123 116 ]!"
```

If used after a `\`, keep the $ char.

```
aya> 10:dollars;
aya> "I have \$$dollars."
"I have $10"
```

If used with anything else, keep the $.

```
aya> "Each apple is worth $0.50"
"Each apple is worth $0.50"
```

Here are some additional examples:

```
aya> 5:num;
aya> 0.75:price;
aya> "I sold $num apples for \$$price each and I made \$$(num price*)"
"I sold 5 apples for $0.75 each and I made $3.75"

aya> "Inner $(\"strings\")"
"Inner strings"

aya> "Inner $(\"$a\") interpolation requires backslashes"
"Inner 1 interpolation requires backslashes"

aya> "Inner-$(\"$(\\\"inner\\\")\") interpolation can be messy"
"Inner-inner interpolation can be messy"
```

## Long String Literals

Long strings are entered using triple quotes. No characters are escaped within long strings. In the following code...

```
"""<div id="my_div">
	<h1>\n: the newline character</h1>
	<p>\{alpha}<p>
	<p>$interpolate</p>
</div>"""
```
...no escape characters are parsed in the output:

```
"<div id="my_div">
	<h1>\n: the newline character</h1>
	<p>\{alpha}<p>
	<p>$interpolate</p>
</div>"
```
