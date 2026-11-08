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
    @Test
    void acceptsValidMethodCall() {
        Program.MethodDeclaration method =
                new Program.MethodDeclaration(
                        "calculate",
                        List.of(
                                new Program.Parameter("value", Type.INT)
                        ),
                        Type.INT,
                        new Statement.Block(List.of(
                                new Statement.ReturnStatement(
                                        new Expression.Variable("value")
                                )
                        ))
                );

        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        new Statement.Block(List.of(
                                new Statement.VariableDeclaration(
                                        Type.INT,
                                        "result",
                                        new Expression.FunctionCall(
                                                "calculate",
                                                List.of(
                                                        new Expression.Literal(
                                                                "10",
                                                                Type.INT
                                                        )
                                                )
                                        )
                                )
                        ))
                );

        Program program = new Program(
                "DJKhaled",
                List.of(),
                List.of(method),
                main
        );

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsUndeclaredMethodCall() {
        Program program = createProgram(List.of(
                new Statement.ExpressionStatement(
                        new Expression.FunctionCall(
                                "missing",
                                List.of()
                        )
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void rejectsIncorrectArgumentCount() {
        Program.MethodDeclaration method =
                new Program.MethodDeclaration(
                        "calculate",
                        List.of(
                                new Program.Parameter("value", Type.INT)
                        ),
                        Type.INT,
                        new Statement.Block(List.of())
                );

        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        new Statement.Block(List.of(
                                new Statement.ExpressionStatement(
                                        new Expression.FunctionCall(
                                                "calculate",
                                                List.of()
                                        )
                                )
                        ))
                );

        Program program = new Program(
                "DJKhaled",
                List.of(),
                List.of(method),
                main
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void rejectsIncorrectArgumentType() {
        Program.MethodDeclaration method =
                new Program.MethodDeclaration(
                        "calculate",
                        List.of(
                                new Program.Parameter("value", Type.INT)
                        ),
                        Type.INT,
                        new Statement.Block(List.of())
                );

        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        new Statement.Block(List.of(
                                new Statement.ExpressionStatement(
                                        new Expression.FunctionCall(
                                                "calculate",
                                                List.of(
                                                        new Expression.Literal(
                                                                "hello",
                                                                Type.STRING
                                                        )
                                                )
                                        )
                                )
                        ))
                );

        Program program = new Program(
                "DJKhaled",
                List.of(),
                List.of(method),
                main
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    private Program createMethodProgram(
            Type returnType,
            Statement... statements
    ) {
        Program.MethodDeclaration method =
                new Program.MethodDeclaration(
                        "calculate",
                        List.of(),
                        returnType,
                        new Statement.Block(List.of(statements))
                );

        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        new Statement.Block(List.of())
                );

        return new Program(
                "DJKhaled",
                List.of(),
                List.of(method),
                main
        );
    }

    @Test
    void acceptsValidIntegerReturn() {
        Program program = createMethodProgram(
                Type.INT,
                new Statement.ReturnStatement(
                        new Expression.Literal("100", Type.INT)
                )
        );

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void acceptsIntegerReturnFromDoubleMethod() {
        Program program = createMethodProgram(
                Type.DOUBLE,
                new Statement.ReturnStatement(
                        new Expression.Literal("100", Type.INT)
                )
        );

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void acceptsValidDoubleReturn() {
        Program program = createMethodProgram(
                Type.DOUBLE,
                new Statement.ReturnStatement(
                        new Expression.Literal("10.5", Type.DOUBLE)
                )
        );

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void acceptsValidStringReturn() {
        Program program = createMethodProgram(
                Type.STRING,
                new Statement.ReturnStatement(
                        new Expression.Literal("\"Major\"", Type.STRING)
                )
        );

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsIncorrectReturnType() {
        Program program = createMethodProgram(
                Type.INT,
                new Statement.ReturnStatement(
                        new Expression.Literal("\"Major\"", Type.STRING)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void rejectsReturnValueFromVoidMethod() {
        Program program = createMethodProgram(
                Type.VOID,
                new Statement.ReturnStatement(
                        new Expression.Literal("100", Type.INT)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void rejectsMissingReturnStatement() {
        Program program = createMethodProgram(
                Type.INT,
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        new Expression.Literal("100", Type.INT)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void rejectsReturnWithoutValueFromNonVoidMethod() {
        Program program = createMethodProgram(
                Type.INT,
                new Statement.ReturnStatement(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void acceptsVoidMethodWithoutReturnStatement() {
        Program program = createMethodProgram(Type.VOID);

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsReturnInsideLoopAsOnlyReturn() {
        Program program = createMethodProgram(
                Type.INT,
                new Statement.WhileLoop(
                        new Expression.Literal("1", Type.INT),
                        new Statement.Block(List.of(
                                new Statement.ReturnStatement(
                                        new Expression.Literal("100", Type.INT)
                                )
                        ))
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }
    @Test
    void acceptsValidWhileCondition() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        new Expression.Literal("10", Type.INT)
                ),
                new Statement.WhileLoop(
                        new Expression.Binary(
                                new Expression.Variable("wins"),
                                Expression.Operator.LESS_THAN,
                                new Expression.Literal("20", Type.INT)
                        ),
                        new Statement.Block(List.of())
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsNonBooleanWhileCondition() {
        Program program = createProgram(List.of(
                new Statement.WhileLoop(
                        new Expression.Literal("10", Type.INT),
                        new Statement.Block(List.of())
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void acceptsValidConditionalCondition() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        new Expression.Literal("10", Type.INT)
                ),
                new Statement.Conditional(
                        new Expression.Binary(
                                new Expression.Variable("wins"),
                                Expression.Operator.GREATER_THAN,
                                new Expression.Literal("5", Type.INT)
                        ),
                        new Statement.Block(List.of()),
                        null
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsNonBooleanConditionalCondition() {
        Program program = createProgram(List.of(
                new Statement.Conditional(
                        new Expression.Literal("10", Type.INT),
                        new Statement.Block(List.of()),
                        null
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

    @Test
    void acceptsValidForLoopCondition() {
        Program program = createProgram(List.of(
                new Statement.ForLoop(
                        new Statement.VariableDeclaration(
                                Type.INT,
                                "i",
                                new Expression.Literal("0", Type.INT)
                        ),
                        new Expression.Binary(
                                new Expression.Variable("i"),
                                Expression.Operator.LESS_THAN,
                                new Expression.Literal("10", Type.INT)
                        ),
                        new Statement.Assignment(
                                "i",
                                new Expression.Binary(
                                        new Expression.Variable("i"),
                                        Expression.Operator.PLUS,
                                        new Expression.Literal("1", Type.INT)
                                )
                        ),
                        new Statement.Block(List.of())
                )
        ));

        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    void rejectsNonBooleanForLoopCondition() {
        Program program = createProgram(List.of(
                new Statement.ForLoop(
                        new Statement.VariableDeclaration(
                                Type.INT,
                                "i",
                                new Expression.Literal("0", Type.INT)
                        ),
                        new Expression.Literal("10", Type.INT),
                        null,
                        new Statement.Block(List.of())
                )
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(program)
        );
    }

}