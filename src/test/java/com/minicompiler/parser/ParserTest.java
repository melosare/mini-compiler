package com.minicompiler.parser;
import com.minicompiler.ast.Expression;
import com.minicompiler.ast.Program;
import com.minicompiler.ast.Statement;
import com.minicompiler.ast.Type;
import com.minicompiler.lexer.Lexer;
import com.minicompiler.lexer.Token;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Program parse(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parseProgram();
    }

    @Test
    void parsesBasicProgram() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                WEOUT
                """;

        Program program = parse(source);

        assertEquals("DJKhaled", program.name());
        assertNotNull(program.mainMethod());
        assertEquals("MAIN", program.mainMethod().name());
    }

    @Test
    void parsesVariableDeclaration() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    BASICB wins GIMMEDAT 100;
                WEOUT
                """;

        Program program = parse(source);

        Statement statement =
                program.mainMethod().body().statements().get(0);

        assertInstanceOf(
                Statement.VariableDeclaration.class,
                statement
        );

        Statement.VariableDeclaration declaration =
                (Statement.VariableDeclaration) statement;

        assertEquals("wins", declaration.name());
        assertEquals(Type.INT, declaration.type());
    }

    @Test
    void parsesArithmeticExpression() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    BASICB wins GIMMEDAT 10 PLUS 5 STACKS 2;
                WEOUT
                """;

        Program program = parse(source);

        Statement.VariableDeclaration declaration =
                (Statement.VariableDeclaration)
                        program.mainMethod().body().statements().get(0);

        Expression.Binary addition =
                (Expression.Binary) declaration.initializer();

        assertEquals(Expression.Operator.PLUS, addition.operator());

        Expression.Binary multiplication =
                (Expression.Binary) addition.right();

        assertEquals(
                Expression.Operator.MULTIPLY,
                multiplication.operator()
        );
    }

    @Test
    void parsesWhileLoop() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    ANOTHERONE : wins SONIN 10
                    LEGGO
                        wins GIMMEDAT wins PLUS 1;
                    WEOUT
                WEOUT
                """;

        Program program = parse(source);

        Statement statement =
                program.mainMethod().body().statements().get(0);

        assertInstanceOf(Statement.WhileLoop.class, statement);
    }

    @Test
    void parsesForLoop() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    WEGOALLDAY WESTSIDE
                        BASICB i GIMMEDAT 0 MO
                        i SONIN 10 MO
                        i GIMMEDAT i PLUS 1
                    EASTSIDE
                    LEGGO
                        THEKEYIS PRINTLN WESTSIDE i EASTSIDE;
                    WEOUT
                WEOUT
                """;

        Program program = parse(source);

        Statement statement =
                program.mainMethod().body().statements().get(0);

        assertInstanceOf(Statement.ForLoop.class, statement);
    }

    @Test
    void parsesMethodDeclarationAndParameters() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY add
                WESTSIDE WEONTHAT a COLON BASICB MO b COLON BASICB
                EASTSIDE COLON BASICB
                LEGGO
                    WEDABEST a PLUS b;
                WEOUT

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                WEOUT
                """;

        Program program = parse(source);

        assertEquals(1, program.methods().size());

        Program.MethodDeclaration method =
                program.methods().get(0);

        assertEquals("add", method.name());
        assertEquals(Type.INT, method.returnType());
        assertEquals(2, method.parameters().size());

        assertEquals("a", method.parameters().get(0).name());
        assertEquals(Type.INT, method.parameters().get(0).type());

        assertEquals("b", method.parameters().get(1).name());
    }

    @Test
    void parsesIfElseStatement() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    BLESS : wins STEPIN 10 UP :
                        LEGGO
                            THEKEYIS PRINTLN WESTSIDE "Major!" EASTSIDE;
                        WEOUT
                    THEYDONTWANTYOUTOSUCCEED :
                        LEGGO
                            THEKEYIS PRINTLN WESTSIDE "Another one!" EASTSIDE;
                        WEOUT
                WEOUT
                """;

        Program program = parse(source);

        Statement statement =
                program.mainMethod().body().statements().get(0);

        assertInstanceOf(Statement.Conditional.class, statement);

        Statement.Conditional conditional =
                (Statement.Conditional) statement;

        assertNotNull(conditional.thenBranch());
        assertNotNull(conditional.elseBranch());
    }
    @Test
    void parsesReturnExpression() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY calculate
                WESTSIDE EASTSIDE COLON BASICB
                LEGGO
                    WEDABEST 10 PLUS 5;
                WEOUT

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                WEOUT
                """;

        Program program = parse(source);

        Program.MethodDeclaration method =
                program.methods().get(0);

        Statement.ReturnStatement statement =
                (Statement.ReturnStatement)
                        method.body().statements().get(0);

        assertInstanceOf(Expression.Binary.class, statement.value());
    }

    @Test
    void parsesFunctionCall() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    THEKEYIS calculate WESTSIDE 10 MO 20 EASTSIDE;
                WEOUT
                """;

        Program program = parse(source);

        Statement statement =
                program.mainMethod().body().statements().get(0);

        assertInstanceOf(
                Statement.ExpressionStatement.class,
                statement
        );

        Statement.ExpressionStatement expressionStatement =
                (Statement.ExpressionStatement) statement;

        assertInstanceOf(
                Expression.FunctionCall.class,
                expressionStatement.expression()
        );

        Expression.FunctionCall call =
                (Expression.FunctionCall) expressionStatement.expression();

        assertEquals("calculate", call.name());
        assertEquals(2, call.arguments().size());
    }

    @Test
    void parsesStringLiteral() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    WORDUP message GIMMEDAT "Another one!";
                WEOUT
                """;

        Program program = parse(source);

        Statement.VariableDeclaration declaration =
                (Statement.VariableDeclaration)
                        program.mainMethod().body().statements().get(0);

        assertEquals(Type.STRING, declaration.type());

        assertInstanceOf(
                Expression.Literal.class,
                declaration.initializer()
        );

        Expression.Literal literal =
                (Expression.Literal) declaration.initializer();

        assertEquals(Type.STRING, literal.type());
    }

    @Test
    void parsesDoubleLiteral() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    HUNDRED money GIMMEDAT 99.99;
                WEOUT
                """;

        Program program = parse(source);

        Statement.VariableDeclaration declaration =
                (Statement.VariableDeclaration)
                        program.mainMethod().body().statements().get(0);

        assertEquals(Type.DOUBLE, declaration.type());

        Expression.Literal literal =
                (Expression.Literal) declaration.initializer();

        assertEquals(Type.DOUBLE, literal.type());
        assertEquals("99.99", literal.value());
    }

    @Test
    void rejectsInvalidExpression() {
        String source = """
                LETMEINSPIREYOU DJKhaled;

                MAJORKEY MAIN WESTSIDE EASTSIDE COLON SCRUB
                LEGGO
                    BASICB wins GIMMEDAT PLUS;
                WEOUT
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parse(source)
        );
    }

    @Test
    void rejectsInvalidProgram() {
        String source = """
                LETMEINSPIREYOU DJKhaled
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parse(source)
        );
    }
}