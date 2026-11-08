package com.minicompiler.codegen.java;

import com.minicompiler.ast.Expression; import com.minicompiler.ast.Program; import com.minicompiler.ast.Statement; import com.minicompiler.ast.Type; import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JavaCodeGeneratorTest {

    private final JavaCodeGenerator generator = new JavaCodeGenerator();

    private Program createProgram(List<Statement> statements) {
        Program.MethodDeclaration main =
                new Program.MethodDeclaration(
                        "MAIN",
                        List.of(),
                        Type.VOID,
                        new Statement.Block(statements)
                );

        return new Program(
                "DJKhaled",
                List.of(),
                List.of(),
                main
        );
    }

    @Test
    void generatesBasicJavaProgram() {
        Program program = createProgram(List.of());

        String result = generator.generate(program);

        assertTrue(result.contains("public class DJKhaled"));
        assertTrue(result.contains("public static void main(String[] args)"));
    }

    @Test
    void generatesVariableDeclaration() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        new Expression.Literal("100", Type.INT)
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains("int wins = 100;"));
    }

    @Test
    void generatesAssignment() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "wins",
                        null
                ),
                new Statement.Assignment(
                        "wins",
                        new Expression.Literal("200", Type.INT)
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains("wins = 200;"));
    }

    @Test
    void generatesArithmeticExpression() {
        Expression expression = new Expression.Binary(
                new Expression.Literal("10", Type.INT),
                Expression.Operator.PLUS,
                new Expression.Binary(
                        new Expression.Literal("5", Type.INT),
                        Expression.Operator.MULTIPLY,
                        new Expression.Literal("2", Type.INT)
                )
        );

        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.INT,
                        "result",
                        expression
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains("(10 + (5 * 2))"));
    }

    @Test
    void generatesPrintStatement() {
        Program program = createProgram(List.of(
                new Statement.PrintStatement(
                        true,
                        List.of(
                                new Expression.Literal(
                                        "\"Hello World\"",
                                        Type.STRING
                                )
                        )
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains(
                "System.out.println(\"Hello World\");"
        ));
    }

    @Test
    void generatesConditional() {
        Program program = createProgram(List.of(
                new Statement.Conditional(
                        new Expression.Binary(
                                new Expression.Literal("10", Type.INT),
                                Expression.Operator.GREATER_THAN,
                                new Expression.Literal("5", Type.INT)
                        ),
                        new Statement.Block(List.of(
                                new Statement.PrintStatement(
                                        true,
                                        List.of(
                                                new Expression.Literal(
                                                        "\"Success\"",
                                                        Type.STRING
                                                )
                                        )
                                )
                        )),
                        null
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains("if ((10 > 5))"));
        assertTrue(result.contains("System.out.println(\"Success\");"));
    }

    @Test
    void generatesWhileLoop() {
        Program program = createProgram(List.of(
                new Statement.WhileLoop(
                        new Expression.Binary(
                                new Expression.Variable("wins"),
                                Expression.Operator.LESS_THAN,
                                new Expression.Literal("10", Type.INT)
                        ),
                        new Statement.Block(List.of(
                                new Statement.Assignment(
                                        "wins",
                                        new Expression.Binary(
                                                new Expression.Variable("wins"),
                                                Expression.Operator.PLUS,
                                                new Expression.Literal("1", Type.INT)
                                        )
                                )
                        ))
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains("while ((wins < 10))"));
        assertTrue(result.contains("wins = (wins + 1);"));
    }

    @Test
    void generatesStringAndDoubleTypes() {
        Program program = createProgram(List.of(
                new Statement.VariableDeclaration(
                        Type.STRING,
                        "name",
                        new Expression.Literal("\"Major\"", Type.STRING)
                ),
                new Statement.VariableDeclaration(
                        Type.DOUBLE,
                        "money",
                        new Expression.Literal("10.5", Type.DOUBLE)
                )
        ));

        String result = generator.generate(program);

        assertTrue(result.contains("String name = \"Major\";"));
        assertTrue(result.contains("double money = 10.5;"));
    }


}