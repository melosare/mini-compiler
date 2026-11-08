package com.minicompiler.ast;
import java.util.List;

public interface Statement {

    record Block(List<Statement> statements) implements Statement {

        public Block {
            statements = List.copyOf(statements);
        }
    }

    record VariableDeclaration(
            Type type,
            String name,
            Expression initializer
    ) implements Statement {}

    record Assignment(
            String name,
            Expression value
    ) implements Statement {}

    record ReturnStatement(
            Expression value
    ) implements Statement {}

    record Conditional(
            Expression condition,
            Statement thenBranch,
            Statement elseBranch
    ) implements Statement {}

    record WhileLoop(
            Expression condition,
            Block body
    ) implements Statement {}

    record ForLoop(
            Statement initializer,
            Expression condition,
            Statement update,
            Block body
    ) implements Statement {}

    record PrintStatement(
            boolean newline,
            List<Expression> arguments
    ) implements Statement {

        public PrintStatement {
            arguments = List.copyOf(arguments);
        }
    }

    record ExpressionStatement(
            Expression expression
    ) implements Statement {}
}
