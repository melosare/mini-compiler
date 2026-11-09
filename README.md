# Mini Compiler

A custom programming language and compiler built from scratch in Java.

Mini Compiler introduces a custom language based DJ Khaled's famous phrases with its own syntax, lexer, parser, abstract syntax tree (AST), semantic analyzer, and Java code generator. Programs written in `.khal` files are compiled into Java source code that can be executed on the JVM.

## Features

- **Custom Language:** A programming language with its own keyword set and syntax.
- **Lexical Analysis:** Converts source code into tokens.
- **Parsing:** Builds an abstract syntax tree (AST) from tokens.
- **Semantic Analysis:** Validates variable declarations, assignments, expressions, method calls, types, and return statements.
- **Java Code Generation:** Translates the AST into Java source code.
- **Automated Testing:** Uses JUnit 5 to test compiler components.

## Technology Stack

- Java 21
- Gradle
- JUnit 5

## Project Structure

```text
mini-compiler/
├── examples/
│   └── hello.khal
├── src/
│   ├── main/
│   │   └── java/com/minicompiler/
│   │       ├── ast/
│   │       ├── cli/
│   │       ├── codegen/
│   │       │   └── java/
│   │       ├── lexer/
│   │       ├── parser/
│   │       ├── semantic/
│   │       └── types/
│   └── test/
│       └── java/com/minicompiler/
│           ├── ast/
│           ├── lexer/
│           ├── parser/
│           └── semantic/
├── build.gradle
├── settings.gradle
└── README.md
```

## Building the Project

Clone the repository:

```bash
git clone YOUR_REPOSITORY_URL
cd mini-compiler
```

Run the test suite:

```bash
./gradlew clean test
```

## Writing a Mini Compiler Program

Create a file with the `.khal` extension.

Example: `examples/hello.khal`

```text
LETMEINSPIREYOU DJKhaled;

MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
LEGGO
    THEKEYIS PRINTLN WESTSIDE "Major Key!" EASTSIDE;
WEOUT
```

## Compiling a Program

Use the compiler CLI to compile a source file:

```bash
./gradlew run --args="examples/hello.khal"
```

The compiler generates Java source code using the `_kahl.java` naming convention.

For example:

```text
hello.khal
    |
    v
hello_kahl.java
```

Compile and execute the generated Java file:

```bash
javac examples/hello_kahl.java
java -cp examples hello_kahl
```

## Running Tests

Run all automated tests:

```bash
./gradlew clean test
```

The test suite covers the lexer, parser, AST, and semantic analyzer.

## Compiler Architecture

The compiler follows a traditional compilation pipeline:

```text
Source Code (.khal)
        |
        v
      Lexer
        |
        v
      Tokens
        |
        v
      Parser
        |
        v
 Abstract Syntax Tree
        |
        v
 Semantic Analyzer
        |
        v
   Java Code Generator
        |
        v
 Generated Java Code
        |
        v
    JVM Execution
```

## Author

Developed as a personal compiler development project.