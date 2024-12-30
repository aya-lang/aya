# Canvas Input

You can poll for mouse and keyboard input using these standard library instructions.

The `graphics.click_events`, `graphics.move_events` and `graphics.typed_chars` instructions provide you with a list of events that occurred
since the last time the instruction was called.

The `graphics.pressed_buttons` and `graphics.pressed_keys` instructions yield the *currently* pressed / held buttons and keys.

## Mouse Events

### Click Events

Lists the mouse clicks since the last time this instruction was executed.

`graphics.click_events` pushes a list of dictionaries with the following keys onto the stack:

- `x` (num) The x-coordinate the click occurred at.
- `y` (num) The y-coordinate the click occurred at.
- `button` (num) The button number the was clicked. (See [Mouse Buttons](#mouse-buttons))
- `clicks` (num) The amount of successive clicks. (Useful for detecting double-clicks)

```
aya> my_canvas.id :(graphics.click_events)
[ {
    128 :x;
    256 :y;
    1 :button;
    3 :clicks;
} ]
```

### Pressed Buttons

Lists the currently held mouse buttons.

`graphics.pressed_buttons` pushes a list of currently held button-numbers onto the stack. (See [Mouse Buttons](#mouse-buttons))

```
aya> my_canvas.id :(graphics.pressed_buttons)
[ 1 3 ]
```

### Move Events

Lists the mouse movements since the last time this instruction was executed.

`graphics.move_events` pushes a list of dictionaries with the following keys onto the stack:

- `x` (num) The x-coordinate the cursor moved to.
- `y` (num) The y-coordinate the cursor moved to.

```
aya> my_canvas.id :(graphics.move_events)
[ {
    128 :x;
    256 :y;
} {
    130 :x;
    260 :y;
} {
    132 :x;
    264 :y;
} ]
```

## Keyboard Events

### Pressed Keys

Lists the currently held keyboard keys.

`graphics.pressed_keys` pushes a list of dictionaries with the following keys onto the stack:

- `key_name` (str) The name of the pressed key. (See [Keyboard Keys](#keyboard-keys))
- `keycode` (num) An integer representation of the key.
- `location_name` (str) The name of the location of the key. (See [Keyboard Locations](#keyboard-locations))
- `location` (num) An integer representation of the location.

```
aya> my_canvas.id :(graphics.pressed_keys)
[ {
    "A" :key_name;
    65 :keycode;
    "STANDARD" :location_name;
    1 :location;
} {
    "CONTROL" :key_name;
    17 :keycode;
    "LEFT" :location_name;
    2 :location;
} ]
```

### Typed Characters

Lists the Unicode characters that were typed since the last time this instruction was executed.

`graphics.typed_chars` pushes a string of typed characters onto the stack.

```
aya> my_canvas.id :(graphics.typed_chars)
"Hello, World!"
```

---

## Overview of possible values

### Mouse Buttons

| Number | Button  |
|--------|---------|
| 1      | left    |
| 2      | middle  |
| 3      | right   |
| 4      | back    |
| 5      | forward |

If your mouse has more than 5 buttons, you may see larger numbers as well.

### Keyboard Keys

| Keycode | Key Name                       |
|---------|--------------------------------|
| 0       | UNDEFINED                      |
| 3       | CANCEL                         |
| 8       | BACK_SPACE                     |
| 9       | TAB                            |
| 10      | ENTER                          |
| 12      | CLEAR                          |
| 16      | SHIFT                          |
| 17      | CONTROL                        |
| 18      | ALT                            |
| 19      | PAUSE                          |
| 20      | CAPS_LOCK                      |
| 21      | KANA                           |
| 24      | FINAL                          |
| 25      | KANJI                          |
| 27      | ESCAPE                         |
| 28      | CONVERT                        |
| 29      | NONCONVERT                     |
| 30      | ACCEPT                         |
| 31      | MODECHANGE                     |
| 32      | SPACE                          |
| 33      | PAGE_UP                        |
| 34      | PAGE_DOWN                      |
| 35      | END                            |
| 36      | HOME                           |
| 37      | LEFT                           |
| 38      | UP                             |
| 39      | RIGHT                          |
| 40      | DOWN                           |
| 44      | COMMA                          |
| 45      | MINUS                          |
| 46      | PERIOD                         |
| 47      | SLASH                          |
| 48      | 0                              |
| 49      | 1                              |
| 50      | 2                              |
| 51      | 3                              |
| 52      | 4                              |
| 53      | 5                              |
| 54      | 6                              |
| 55      | 7                              |
| 56      | 8                              |
| 57      | 9                              |
| 59      | SEMICOLON                      |
| 61      | EQUALS                         |
| 65      | A                              |
| 66      | B                              |
| 67      | C                              |
| 68      | D                              |
| 69      | E                              |
| 70      | F                              |
| 71      | G                              |
| 72      | H                              |
| 73      | I                              |
| 74      | J                              |
| 75      | K                              |
| 76      | L                              |
| 77      | M                              |
| 78      | N                              |
| 79      | O                              |
| 80      | P                              |
| 81      | Q                              |
| 82      | R                              |
| 83      | S                              |
| 84      | T                              |
| 85      | U                              |
| 86      | V                              |
| 87      | W                              |
| 88      | X                              |
| 89      | Y                              |
| 90      | Z                              |
| 91      | OPEN_BRACKET                   |
| 92      | BACK_SLASH                     |
| 93      | CLOSE_BRACKET                  |
| 96      | NUMPAD0                        |
| 97      | NUMPAD1                        |
| 98      | NUMPAD2                        |
| 99      | NUMPAD3                        |
| 100     | NUMPAD4                        |
| 101     | NUMPAD5                        |
| 102     | NUMPAD6                        |
| 103     | NUMPAD7                        |
| 104     | NUMPAD8                        |
| 105     | NUMPAD9                        |
| 106     | MULTIPLY                       |
| 107     | ADD                            |
| 108     | SEPARATOR                      |
| 109     | SUBTRACT                       |
| 110     | DECIMAL                        |
| 111     | DIVIDE                         |
| 112     | F1                             |
| 113     | F2                             |
| 114     | F3                             |
| 115     | F4                             |
| 116     | F5                             |
| 117     | F6                             |
| 118     | F7                             |
| 119     | F8                             |
| 120     | F9                             |
| 121     | F10                            |
| 122     | F11                            |
| 123     | F12                            |
| 127     | DELETE                         |
| 128     | DEAD_GRAVE                     |
| 129     | DEAD_ACUTE                     |
| 130     | DEAD_CIRCUMFLEX                |
| 131     | DEAD_TILDE                     |
| 132     | DEAD_MACRON                    |
| 133     | DEAD_BREVE                     |
| 134     | DEAD_ABOVEDOT                  |
| 135     | DEAD_DIAERESIS                 |
| 136     | DEAD_ABOVERING                 |
| 137     | DEAD_DOUBLEACUTE               |
| 138     | DEAD_CARON                     |
| 139     | DEAD_CEDILLA                   |
| 140     | DEAD_OGONEK                    |
| 141     | DEAD_IOTA                      |
| 142     | DEAD_VOICED_SOUND              |
| 143     | DEAD_SEMIVOICED_SOUND          |
| 144     | NUM_LOCK                       |
| 145     | SCROLL_LOCK                    |
| 150     | AMPERSAND                      |
| 151     | ASTERISK                       |
| 152     | QUOTEDBL                       |
| 153     | LESS                           |
| 154     | PRINTSCREEN                    |
| 155     | INSERT                         |
| 156     | HELP                           |
| 157     | META                           |
| 160     | GREATER                        |
| 161     | BRACELEFT                      |
| 162     | BRACERIGHT                     |
| 192     | BACK_QUOTE                     |
| 222     | QUOTE                          |
| 224     | KP_UP                          |
| 225     | KP_DOWN                        |
| 226     | KP_LEFT                        |
| 227     | KP_RIGHT                       |
| 240     | ALPHANUMERIC                   |
| 241     | KATAKANA                       |
| 242     | HIRAGANA                       |
| 243     | FULL_WIDTH                     |
| 244     | HALF_WIDTH                     |
| 245     | ROMAN_CHARACTERS               |
| 256     | ALL_CANDIDATES                 |
| 257     | PREVIOUS_CANDIDATE             |
| 258     | CODE_INPUT                     |
| 259     | JAPANESE_KATAKANA              |
| 260     | JAPANESE_HIRAGANA              |
| 261     | JAPANESE_ROMAN                 |
| 262     | KANA_LOCK                      |
| 263     | INPUT_METHOD_ON_OFF            |
| 512     | AT                             |
| 513     | COLON                          |
| 514     | CIRCUMFLEX                     |
| 515     | DOLLAR                         |
| 516     | EURO_SIGN                      |
| 517     | EXCLAMATION_MARK               |
| 518     | INVERTED_EXCLAMATION_MARK      |
| 519     | LEFT_PARENTHESIS               |
| 520     | NUMBER_SIGN                    |
| 521     | PLUS                           |
| 522     | RIGHT_PARENTHESIS              |
| 523     | UNDERSCORE                     |
| 524     | WINDOWS                        |
| 525     | CONTEXT_MENU                   |
| 61440   | F13                            |
| 61441   | F14                            |
| 61442   | F15                            |
| 61443   | F16                            |
| 61444   | F17                            |
| 61445   | F18                            |
| 61446   | F19                            |
| 61447   | F20                            |
| 61448   | F21                            |
| 61449   | F22                            |
| 61450   | F23                            |
| 61451   | F24                            |
| 65312   | COMPOSE                        |
| 65368   | BEGIN                          |
| 65406   | ALT_GRAPH                      |
| 65480   | STOP                           |
| 65481   | AGAIN                          |
| 65482   | PROPS                          |
| 65483   | UNDO                           |
| 65485   | COPY                           |
| 65487   | PASTE                          |
| 65488   | FIND                           |
| 65489   | CUT                            |

For more information, check the [KeyEvent javadoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/event/KeyEvent.html)

### Keyboard Locations

| Location Code | Location Name |
|---------------|---------------|
| 0             | UNKNOWN       |
| 1             | STANDARD      |
| 2             | LEFT          |
| 3             | RIGHT         |
| 4             | NUMPAD        |

For more information, check the [KeyEvent javadoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/event/KeyEvent.html)