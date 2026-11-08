package com.minicompiler.parser;

import com.minicompiler.ast.Expression;
import com.minicompiler.ast.Program;
import com.minicompiler.ast.Statement;
import com.minicompiler.ast.Type;
import com.minicompiler.lexer.Token;
import com.minicompiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException("Token list cannot be empty.");
        }

        this.tokens = List.copyOf(tokens);
    }

    public Program parseProgram() {
        return parse();
    }

    public Program parse() {
        String programName = parseProgramDeclaration();

        List<String> imports = new ArrayList<>();

        while (match(TokenType.HINGES)) {
            imports.addAll(parseImportDeclaration());
        }

        List<Program.MethodDeclaration> methods = new ArrayList<>();

        while (check(TokenType.MAJORKEY)
                && !checkNext(TokenType.MAIN)) {
            methods.add(parseMethodDeclaration());
        }

        Program.MethodDeclaration mainMethod = parseMainDeclaration();

        consume(TokenType.EOF, "Expected end of file.");

        return new Program(programName, imports, methods, mainMethod);
    }

    private String parseProgramDeclaration() {
        consume(TokenType.LETMEINSPIREYOU, "Expected LETMEINSPIREYOU.");

        String name = consume(
                TokenType.IDENTIFIER,
                "Expected program name."
        ).lexeme();

        consume(TokenType.SEMIC, "Expected ';' after program declaration.");

        return name;
    }

    private List<String> parseImportDeclaration() {
        List<String> imports = new ArrayList<>();

        imports.add(consume(
                TokenType.IDENTIFIER,
                "Expected import name."
        ).lexeme());

        while (match(TokenType.MO)) {
            imports.add(consume(
                    TokenType.IDENTIFIER,
                    "Expected import name after comma."
            ).lexeme());
        }

        consume(TokenType.SEMIC, "Expected ';' after imports.");

        return imports;
    }

    private Program.MethodDeclaration parseMethodDeclaration() {
        consume(TokenType.MAJORKEY, "Expected MAJORKEY.");

        String name = consume(
                TokenType.IDENTIFIER,
                "Expected method name."
        ).lexeme();

        consume(TokenType.WESTSIDE, "Expected '('.");

        List<Program.Parameter> parameters = new ArrayList<>();

        if (match(TokenType.WEONTHAT)) {
            parameters.add(parseParameter());

            while (match(TokenType.MO)) {
                parameters.add(parseParameter());
            }
        }

        consume(TokenType.EASTSIDE, "Expected ')'.");
        consume(TokenType.COLON, "Expected ':'.");

        Type returnType = parseType();
        Statement.Block body = parseBlock();

        return new Program.MethodDeclaration(
                name, parameters, returnType, body
        );
    }

    private Program.MethodDeclaration parseMainDeclaration() {
        consume(TokenType.MAJORKEY, "Expected MAJORKEY.");
        consume(TokenType.MAIN, "Expected MAIN.");
        consume(TokenType.WESTSIDE, "Expected '('.");
        consume(TokenType.EASTSIDE, "Expected ')'.");
        consume(TokenType.COLON, "Expected ':'.");

        Type returnType = parseType();

        if (returnType != Type.VOID) {
            throw error(previous(), "MAIN must have type SCRUB.");
        }

        Statement.Block body = parseBlock();

        return new Program.MethodDeclaration(
                "MAIN", List.of(), returnType, body
        );
    }

    private Program.Parameter parseParameter() {
        String name = consume(
                TokenType.IDENTIFIER,
                "Expected parameter name."
        ).lexeme();

        consume(TokenType.COLON, "Expected ':' after parameter name.");

        return new Program.Parameter(name, parseType());
    }

    private Type parseType() {
        if (match(TokenType.BASICB)) {
            return Type.INT;
        }

        if (match(TokenType.HUNDRED)) {
            return Type.DOUBLE;
        }

        if (match(TokenType.WORDUP)) {
            return Type.STRING;
        }

        if (match(TokenType.SCRUB)) {
            return Type.VOID;
        }

        throw error(peek(), "Expected a data type.");
    }

    private Statement.Block parseBlock() {
        consume(TokenType.LEGGO, "Expected LEGGO.");

        List<Statement> statements = new ArrayList<>();

        while (!check(TokenType.WEOUT) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        consume(TokenType.WEOUT, "Expected WEOUT.");

        return new Statement.Block(statements);
    }

    private Statement parseStatement() {
        if (check(TokenType.LEGGO)) {
            return parseBlock();
        }

        if (isType(peek().type())) {
            return parseVariableDeclaration();
        }

        if (match(TokenType.WEDABEST)) {
            return parseReturnStatement();
        }

        if (match(TokenType.BLESS)) {
            return parseConditional();
        }

        if (match(TokenType.ANOTHERONE)) {
            return parseWhileLoop();
        }

        if (match(TokenType.WEGOALLDAY)) {
            return parseForLoop();
        }

        if (match(TokenType.THEKEYIS)) {
            return parseOutputOrFunctionCall();
        }

        if (check(TokenType.IDENTIFIER)) {
            return parseAssignment();
        }

        throw error(peek(), "Expected a statement.");
    }

    private Statement parseVariableDeclaration() {
        Type type = parseType();

        String name = consume(
                TokenType.IDENTIFIER,
                "Expected variable name."
        ).lexeme();

        Expression initializer = null;

        if (match(TokenType.GIMMEDAT)) {
            initializer = parseExpression();
        }

        consume(TokenType.SEMIC, "Expected ';' after variable declaration.");

        return new Statement.VariableDeclaration(type, name, initializer);
    }

    private Statement parseAssignment() {
        String name = consume(
                TokenType.IDENTIFIER,
                "Expected variable name."
        ).lexeme();

        consume(TokenType.GIMMEDAT, "Expected GIMMEDAT.");

        Expression value = parseExpression();

        consume(TokenType.SEMIC, "Expected ';' after assignment.");

        return new Statement.Assignment(name, value);
    }

    private Statement parseReturnStatement() {
        Expression value = null;

        if (!check(TokenType.SEMIC)) {
            value = parseExpression();
        }

        consume(TokenType.SEMIC, "Expected ';' after return statement.");

        return new Statement.ReturnStatement(value);
    }

    private Statement parseConditional() {
        consume(TokenType.COLON, "Expected ':' after BLESS.");

        Expression condition = parseBooleanExpression();

        consume(TokenType.UP, "Expected UP.");
        consume(TokenType.COLON, "Expected ':' after UP.");

        Statement thenBranch = parseStatement();
        Statement elseBranch = null;

        if (match(TokenType.THEYDONTWANTYOUTOSUCCEED)) {
            consume(TokenType.COLON, "Expected ':' after ELSE.");
            elseBranch = parseStatement();
        }

        return new Statement.Conditional(
                condition, thenBranch, elseBranch
        );
    }

    private Statement parseWhileLoop() {
        consume(TokenType.COLON, "Expected ':' after ANOTHERONE.");

        Expression condition = parseBooleanExpression();
        Statement.Block body = parseBlock();

        return new Statement.WhileLoop(condition, body);
    }

    private Statement parseForLoop() {
        consume(TokenType.WESTSIDE, "Expected '(' after WEGOALLDAY.");

        Statement initializer = parseForInitializer();

        consume(TokenType.MO, "Expected ',' after for initializer.");

        Expression condition = parseBooleanExpression();

        consume(TokenType.MO, "Expected ',' after for condition.");

        Statement update = parseForUpdate();

        consume(TokenType.EASTSIDE, "Expected ')' after for update.");

        Statement.Block body = parseBlock();

        return new Statement.ForLoop(
                initializer, condition, update, body
        );
    }

    private Statement parseForInitializer() {
        if (isType(peek().type())) {
            Type type = parseType();

            String name = consume(
                    TokenType.IDENTIFIER,
                    "Expected variable name."
            ).lexeme();

            consume(TokenType.GIMMEDAT, "Expected GIMMEDAT.");

            Expression value = parseExpression();

            return new Statement.VariableDeclaration(type, name, value);
        }

        String name = consume(
                TokenType.IDENTIFIER,
                "Expected variable name."
        ).lexeme();

        consume(TokenType.GIMMEDAT, "Expected GIMMEDAT.");

        Expression value = parseExpression();

        return new Statement.Assignment(name, value);
    }
    private Statement parseForUpdate() {
        if (check(TokenType.IDENTIFIER)
                && checkNext(TokenType.GIMMEDAT)) {

            String name = advance().lexeme();

            consume(TokenType.GIMMEDAT, "Expected GIMMEDAT.");

            Expression value = parseExpression();

            return new Statement.Assignment(name, value);
        }

        return new Statement.ExpressionStatement(parseExpression());
    }

    private Statement parseOutputOrFunctionCall() {
        if (match(TokenType.PRINT)) {
            return parsePrintStatement(false);
        }

        if (match(TokenType.PRINTLN)) {
            return parsePrintStatement(true);
        }

        Expression.FunctionCall call = parseFunctionCall();

        consume(TokenType.SEMIC, "Expected ';' after function call.");

        return new Statement.ExpressionStatement(call);
    }

    private Statement parsePrintStatement(boolean newline) {
        consume(TokenType.WESTSIDE, "Expected '(' after PRINT.");

        List<Expression> arguments = parseArgumentList();

        consume(TokenType.EASTSIDE, "Expected ')' after print arguments.");
        consume(TokenType.SEMIC, "Expected ';' after print statement.");

        return new Statement.PrintStatement(newline, arguments);
    }

    private Expression.FunctionCall parseFunctionCall() {
        String name = consume(
                TokenType.IDENTIFIER,
                "Expected function name."
        ).lexeme();

        consume(TokenType.WESTSIDE, "Expected '(' after function name.");

        List<Expression> arguments = parseArgumentList();

        consume(TokenType.EASTSIDE, "Expected ')' after function arguments.");

        return new Expression.FunctionCall(name, arguments);
    }

    private List<Expression> parseArgumentList() {
        List<Expression> arguments = new ArrayList<>();

        if (check(TokenType.EASTSIDE)) {
            return arguments;
        }

        arguments.add(parseExpression());

        while (match(TokenType.MO)) {
            arguments.add(parseExpression());
        }

        return arguments;
    }

    private Expression parseBooleanExpression() {
        Expression left = parseExpression();

        Expression.Operator operator;

        if (match(TokenType.STEPIN)) {
            operator = Expression.Operator.GREATER_THAN;
        } else if (match(TokenType.SONIN)) {
            operator = Expression.Operator.LESS_THAN;
        } else if (match(TokenType.PEEPS)) {
            operator = Expression.Operator.EQUAL;
        } else {
            throw error(peek(), "Expected comparison operator.");
        }

        Expression right = parseExpression();

        return new Expression.Binary(left, operator, right);
    }

    private Expression parseExpression() {
        return parseAdditive();
    }

    private Expression parseAdditive() {
        Expression expression = parseMultiplicative();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType operator = previous().type();

            Expression right = parseMultiplicative();

            expression = new Expression.Binary(
                    expression,
                    operator == TokenType.PLUS
                            ? Expression.Operator.PLUS
                            : Expression.Operator.MINUS,
                    right
            );
        }

        return expression;
    }

    private Expression parseMultiplicative() {
        Expression expression = parsePrimary();

        while (match(TokenType.STACKS, TokenType.SPLIT)) {
            TokenType operator = previous().type();

            Expression right = parsePrimary();

            expression = new Expression.Binary(
                    expression,
                    operator == TokenType.STACKS
                            ? Expression.Operator.MULTIPLY
                            : Expression.Operator.DIVIDE,
                    right
            );
        }

        return expression;
    }

    private Expression parsePrimary() {
        if (match(TokenType.NUMBER)) {
            String value = previous().lexeme();

            Type type = value.contains(".")
                    ? Type.DOUBLE
                    : Type.INT;

            return new Expression.Literal(value, type);
        }

        if (match(TokenType.STRING)) {
            return new Expression.Literal(
                    previous().lexeme(),
                    Type.STRING
            );
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Expression.Variable(previous().lexeme());
        }

        if (match(TokenType.THEKEYIS)) {
            return parseFunctionCall();
        }

        if (match(TokenType.WESTSIDE)) {
            Expression expression = parseExpression();

            consume(TokenType.EASTSIDE, "Expected ')' after expression.");

            return expression;
        }

        throw error(peek(), "Expected an expression.");
    }

    private boolean isType(TokenType type) {
        return type == TokenType.BASICB
                || type == TokenType.HUNDRED
                || type == TokenType.WORDUP
                || type == TokenType.SCRUB;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }

        return peek().type() == type;
    }

    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size()) {
            return false;
        }

        return tokens.get(current + 1).type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private IllegalArgumentException error(Token token, String message) {
        return new IllegalArgumentException(
                "Parser error at line " + token.line()
                        + ", column " + token.column()
                        + ": " + message
                        + " Found " + token.type()
                        + " ('" + token.lexeme() + "')."
        );
    }
}
