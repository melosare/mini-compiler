package com.minicompiler.lexer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    private int line = 1;
    private int column = 1;

    private int startLine = 1;
    private int startColumn = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {

        while (!isAtEnd()) {

            start = current;
            startLine = line;
            startColumn = column;

            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", line, column));

        return tokens;
    }

    private void scanToken() {

        char c = advance();

        switch (c) {

            // Whitespace
            case ' ', '\t', '\f', '\r', '\n' -> {
                // Ignore whitespace
            }

            // Single-character punctuation
            case '(' -> addToken(TokenType.WESTSIDE);
            case ')' -> addToken(TokenType.EASTSIDE);
            case ',' -> addToken(TokenType.MO);
            case ':' -> addToken(TokenType.COLON);
            case ';' -> addToken(TokenType.SEMIC);

            // Operators
            case '+' -> addToken(TokenType.PLUS);
            case '-' -> addToken(TokenType.MINUS);
            case '*' -> addToken(TokenType.STACKS);
            case '/' -> addToken(TokenType.SPLIT);
            case '>' -> addToken(TokenType.STEPIN);
            case '<' -> addToken(TokenType.SONIN);
            case '=' -> addToken(TokenType.PEEPS);

            // String literals
            case '"' -> string();

            default -> {

                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw error("Unexpected character '" + c + "'");
                }
            }
        }
    }

    private void identifier() {

        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);

        TokenType type = keywordType(text);

        addToken(type);
    }

    private TokenType keywordType(String text) {

        return switch (text.toUpperCase(Locale.ROOT)) {

            case "LETMEINSPIREYOU" -> TokenType.LETMEINSPIREYOU;
            case "HINGES" -> TokenType.HINGES;
            case "MAJORKEY" -> TokenType.MAJORKEY;
            case "MAIN" -> TokenType.MAIN;

            case "LEGGO" -> TokenType.LEGGO;
            case "WEOUT" -> TokenType.WEOUT;
            case "WEONTHAT" -> TokenType.WEONTHAT;

            case "BASICB" -> TokenType.BASICB;
            case "HUNDRED" -> TokenType.HUNDRED;
            case "WORDUP" -> TokenType.WORDUP;
            case "SCRUB" -> TokenType.SCRUB;

            case "GIMMEDAT" -> TokenType.GIMMEDAT;
            case "WEDABEST" -> TokenType.WEDABEST;
            case "BLESS" -> TokenType.BLESS;
            case "UP" -> TokenType.UP;
            case "THEYDONTWANTYOUTOSUCCEED" ->
                    TokenType.THEYDONTWANTYOUTOSUCCEED;

            case "ANOTHERONE" -> TokenType.ANOTHERONE;
            case "WEGOALLDAY" -> TokenType.WEGOALLDAY;

            case "THEKEYIS" -> TokenType.THEKEYIS;
            case "PRINT" -> TokenType.PRINT;
            case "PRINTLN" -> TokenType.PRINTLN;

            case "PLUS" -> TokenType.PLUS;
            case "MINUS" -> TokenType.MINUS;
            case "STACKS" -> TokenType.STACKS;
            case "SPLIT" -> TokenType.SPLIT;
            case "STEPIN" -> TokenType.STEPIN;
            case "SONIN" -> TokenType.SONIN;
            case "PEEPS" -> TokenType.PEEPS;

            case "WESTSIDE" -> TokenType.WESTSIDE;
            case "EASTSIDE" -> TokenType.EASTSIDE;
            case "MO" -> TokenType.MO;
            case "COLON" -> TokenType.COLON;
            case "SEMIC" -> TokenType.SEMIC;

            default -> TokenType.IDENTIFIER;
        };
    }

    private void number() {

        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {

            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(TokenType.NUMBER);
    }

    private void string() {

        while (!isAtEnd() && peek() != '"') {

            if (peek() == '\n' || peek() == '\r') {
                throw error("Unterminated string literal");
            }

            if (peek() == '\\') {
                advance();

                if (isAtEnd()) {
                    throw error("Unterminated string escape");
                }
            }

            advance();
        }

        if (isAtEnd()) {
            throw error("Unterminated string literal");
        }

        // Consume closing quotation mark
        advance();

        addToken(TokenType.STRING);
    }

    private void addToken(TokenType type) {

        String lexeme = source.substring(start, current);

        tokens.add(new Token(
                type,
                lexeme,
                startLine,
                startColumn
        ));
    }

    private char advance() {

        char c = source.charAt(current++);

        if (c == '\r') {

            if (current < source.length()
                    && source.charAt(current) == '\n') {
                current++;
            }

            line++;
            column = 1;

            return '\n';
        }

        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }

        return c;
    }

    private boolean match(char expected) {

        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != expected) {
            return false;
        }

        advance();

        return true;
    }

    private char peek() {

        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private char peekNext() {

        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {

        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaNumeric(char c) {

        return isAlpha(c)
                || isDigit(c)
                || c == '_';
    }

    private IllegalArgumentException error(String message) {

        return new IllegalArgumentException(
                "Lexer error at line " + startLine
                        + ", column " + startColumn
                        + ": " + message
        );
    }
}
