package com.minicompiler.ast;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AstTest {

    @Test
    void createsVariableDeclaration() {

        Expression initializer =
                new Expression.Literal("100", Type.INT);

        Statement.VariableDeclaration declaration =
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        initializer
                );

        assertEquals(Type.INT, declaration.type());
        assertEquals("wins", declaration.name());
        assertEquals(initializer, declaration.initializer());
    }

    @Test
    void createsBinaryExpression() {

        Expression left =
                new Expression.Variable("wins");

        Expression right =
                new Expression.Literal("1", Type.INT);

        Expression.Binary expression =
                new Expression.Binary(
                        left,
                        Expression.Operator.PLUS,
                        right
                );

        assertEquals(Expression.Operator.PLUS, expression.operator());
        assertEquals(left, expression.left());
        assertEquals(right, expression.right());
    }

    @Test
    void createsWhileLoop() {

        Expression condition =
                new Expression.Binary(
                        new Expression.Variable("wins"),
                        Expression.Operator.LESS_THAN,
                        new Expression.Literal("10", Type.INT)
                );

        Statement.Block body =
                new Statement.Block(List.of());

        Statement.WhileLoop loop =
                new Statement.WhileLoop(condition, body);

        assertEquals(condition, loop.condition());
        assertEquals(body, loop.body());
    }

    @Test
    void createsProgram() {

        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        new Statement.Block(List.of())
                );

        Program program =
                new Program(
                        "DJKhaled",
                        List.of(),
                        List.of(),
                        main
                );

        assertEquals("DJKhaled", program.name());
        assertEquals("MAIN", program.mainMethod().name());
        assertEquals(Type.VOID, program.mainMethod().returnType());
    }
}
