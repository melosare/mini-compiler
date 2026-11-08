package com.minicompiler.semantic;
import com.minicompiler.ast.Expression;
import com.minicompiler.ast.Program;
import com.minicompiler.ast.Statement;
import com.minicompiler.ast.Type;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SemanticAnalyzerTest {

    private final SemanticAnalyzer analyzer = new SemanticAnalyzer();

    private Program createProgram(List<Statement> statements) {
        Statement.Block body = new Statement.Block(statements);

        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        body
                );

        return new Program(
                "DJKhaled",
                List.of(),
                List.of(),
                main
        );
    }

    @Test
    void acceptsValidVariableDeclaration() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        new Expression.Literal("100", Type.INT)
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsDuplicateVariableDeclaration() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT, "wins", null
                ),
                new Statement.VariableDeclaration(
                        Type.INT, "wins", null
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void rejectsUndeclaredVariable() {
        Program program = createProgram(List.of(
                new Statement.Assignment(
                        "wins",
                        new Expression.Literal("100", Type.INT)
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void acceptsValidAssignment() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        null
                ),
                new Statement.Assignment(
                        "wins",
                        new Expression.Literal("100", Type.INT)
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsIncompatibleAssignment() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        null
                ),
                new Statement.Assignment(
                        "wins",
                        new Expression.Literal("Major", Type.STRING)
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void acceptsIntegerToDoubleAssignment() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.DOUBLE,
                        "money",
                        new Expression.Literal("100", Type.INT)
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsUndeclaredVariableInExpression() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        new Expression.Variable("missing")
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void acceptsArithmeticExpression() {
        Expression expression = new Expression.Binary(
                new Expression.Literal("10", Type.INT),
                Expression.Operator.PLUS,
                new Expression.Literal("5", Type.INT)
        );

        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        expression
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsInvalidArithmeticExpression() {
        Expression expression = new Expression.Binary(
                new Expression.Literal("Major", Type.STRING),
                Expression.Operator.MINUS,
                new Expression.Literal("5", Type.INT)
        );

        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        expression
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }
}