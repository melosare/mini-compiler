package com.minicompiler.lexer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void recognizesKeywords() {

        Lexer lexer = new Lexer(
                "LETMEINSPIREYOU MAJORKEY BASICB ANOTHERONE WEGOALLDAY"
        );

        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.LETMEINSPIREYOU, tokens.get(0).type());
        assertEquals(TokenType.MAJORKEY, tokens.get(1).type());
        assertEquals(TokenType.BASICB, tokens.get(2).type());
        assertEquals(TokenType.ANOTHERONE, tokens.get(3).type());
        assertEquals(TokenType.WEGOALLDAY, tokens.get(4).type());
        assertEquals(TokenType.EOF, tokens.get(5).type());
    }

    @Test
    void recognizesIdentifiers() {

        Lexer lexer = new Lexer("wins myVariable variable_1");

        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type());
        assertEquals("wins", tokens.get(0).lexeme());

        assertEquals("myVariable", tokens.get(1).lexeme());
        assertEquals("variable_1", tokens.get(2).lexeme());
    }

    @Test
    void recognizesNumbers() {

        Lexer lexer = new Lexer("100 25.50 0");

        List<Token> tokens = lexer.tokenize();

        assertEquals("100", tokens.get(0).lexeme());
        assertEquals("25.50", tokens.get(1).lexeme());
        assertEquals("0", tokens.get(2).lexeme());

        assertEquals(TokenType.NUMBER, tokens.get(0).type());
    }

    @Test
    void recognizesStrings() {

        Lexer lexer = new Lexer("\"We the best!\"");

        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.STRING, tokens.get(0).type());
        assertEquals("\"We the best!\"", tokens.get(0).lexeme());
    }

    @Test
    void recognizesOperators() {

        Lexer lexer = new Lexer(
                "GIMMEDAT + - * / > < ="
        );

        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.GIMMEDAT, tokens.get(0).type());
        assertEquals(TokenType.PLUS, tokens.get(1).type());
        assertEquals(TokenType.MINUS, tokens.get(2).type());
        assertEquals(TokenType.STACKS, tokens.get(3).type());
        assertEquals(TokenType.SPLIT, tokens.get(4).type());
        assertEquals(TokenType.STEPIN, tokens.get(5).type());
        assertEquals(TokenType.SONIN, tokens.get(6).type());
        assertEquals(TokenType.PEEPS, tokens.get(7).type());
    }

    @Test
    void tracksLineAndColumn() {

        Lexer lexer = new Lexer(
                "BASICB wins;\nWORDUP message;"
        );

        List<Token> tokens = lexer.tokenize();

        assertEquals(1, tokens.get(0).line());
        assertEquals(1, tokens.get(0).column());

        assertEquals(2, tokens.get(3).line());
        assertEquals(1, tokens.get(3).column());
    }

    @Test
    void rejectsUnexpectedCharacters() {

        Lexer lexer = new Lexer("@");

        assertThrows(
                IllegalArgumentException.class,
                lexer::tokenize
        );
    }
}
