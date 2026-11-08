
# mini-compiler Language Specification

Version: 1.0 

## 1. Overview

The mini-compiler contains a small programming language inspired by DJ Khaled's
signature phrases.

The initial implementation compiles mini-compiler source code into Java.

## 2. Program Structure

Every program begins with a program declaration.

Example:

    LETMEINSPIREYOU DJKhaled;

A program must contain exactly one main method.

The main method uses the following structure:

    MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
    LEGGO
        (* statements go here *)
    WEOUT

## 3. Data Types

The language supports four basic types.

| Keyword | Java equivalent |
|---------|-----------------|
| BASICB  | int             |
| HUNDRED | double          |
| WORDUP  | String          |
| SCRUB   | void            |

## 4. Variables

Variables are declared using a type followed by an identifier.

Example:

    BASICB wins;

Variables can also be initialized during declaration.

    BASICB wins GIMMEDAT 100;

Assignment uses GIMMEDAT.

    wins GIMMEDAT 200;

## 5. Arithmetic Operators

| Keyword | Operator |
|---------|----------|
| PLUS    | +        |
| MINUS   | -        |
| STACKS  | *        |
| SPLIT   | /        |

Multiplication and division have higher precedence than addition
and subtraction.

Parentheses can be used to group expressions.

## 6. Comparison Operators

| Keyword | Operator |
|---------|----------|
| STEPIN  | >        |
| SONIN   | <        |
| PEEPS   | ==       |

PEEPS represents equality in conditional expressions.

## 7. Conditional Statements

BLESS introduces a conditional statement.

Example:

    BLESS : wins STEPIN 100 UP :
        THEKEYIS PRINTLN WESTSIDE "We the best!" EASTSIDE;

An optional else branch uses THEYDONTWANTYOUTOSUCCEED.

    BLESS : wins SONIN 100 UP :
        THEKEYIS PRINTLN WESTSIDE "Keep going!" EASTSIDE
    THEYDONTWANTYOUTOSUCCEED :
        THEKEYIS PRINTLN WESTSIDE "Another one!" EASTSIDE

## 8. Loops

### 8.1 While Loop

ANOTHERONE introduces a while loop.

Example:

    ANOTHERONE : wins SONIN 10
    LEGGO
        wins GIMMEDAT wins PLUS 1;
    WEOUT

The condition is evaluated before each iteration.

### 8.2 For Loop

WEGOALLDAY introduces a for loop.

The syntax contains an initializer, condition, and update expression.

Example:

    WEGOALLDAY WESTSIDE BASICB i GIMMEDAT 0 MO i SONIN 10 MO i GIMMEDAT i PLUS 1 EASTSIDE
    LEGGO
        THEKEYIS PRINTLN WESTSIDE i EASTSIDE;
    WEOUT

The loop initializer executes once.

The condition is evaluated before each iteration.

The update expression executes after each iteration.

## 9. Methods

MAJORKEY introduces a method declaration.

Example:

    MAJORKEY calculate WESTSIDE WEONTHAT x COLON BASICB EASTSIDE COLON BASICB
    LEGGO
        WEDABEST x STACKS 2;
    WEOUT

Methods can accept parameters and declare a return type.

## 10. Function Calls

THEKEYIS introduces a function call.

Example:

    THEKEYIS calculate WESTSIDE 10 EASTSIDE;

## 11. Output

PRINT displays output without adding a newline.

PRINTLN displays output followed by a newline.

Examples:

    THEKEYIS PRINT WESTSIDE "Hello!" EASTSIDE;

    THEKEYIS PRINTLN WESTSIDE "We the best!" EASTSIDE;

Calling either output operation without arguments uses the language's
default output phrase.

## 12. Imports

HINGES introduces import declarations.

Example:

    HINGES math, utilities;

Import declarations appear after the program declaration.

## 13. Comments

Comment syntax is reserved for a future language specification update.

## 14. Compilation Target

The initial compiler generates Java source code.

The generated Java source can subsequently be compiled using the
standard Java compiler.

## 15. Language Limitations

The initial version intentionally keeps the language small.

Features outside this specification are not currently supported.
