package com.minicompiler.ast;
import java.util.List;

public interface Expression {

    record Literal(String value, Type type) implements Expression {}

    record Variable(String name) implements Expression {}

    record Binary(
            Expression left,
            Operator operator,
            Expression right
    ) implements Expression {}

    record FunctionCall(
            String name,
            List<Expression> arguments
    ) implements Expression {

        public FunctionCall {
            arguments = List.copyOf(arguments);
        }
    }

    enum Operator {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        GREATER_THAN,
        LESS_THAN,
        EQUAL
    }
}
