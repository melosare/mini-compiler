package com.minicompiler.semantic;
import com.minicompiler.ast.Expression;
import com.minicompiler.ast.Program;
import com.minicompiler.ast.Statement;
import com.minicompiler.ast.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SemanticAnalyzer {

    private final Deque<Map<String, Type>> scopes = new ArrayDeque<>();

    public void analyze(Program program) {
        scopes.clear();

        for (Program.MethodDeclaration method : program.methods()) {
            analyzeMethod(method);
        }

        analyzeMethod(program.mainMethod());
    }

    private void analyzeMethod(Program.MethodDeclaration method) {
        scopes.clear();
        beginScope();

        for (Program.Parameter parameter : method.parameters()) {
            declare(parameter.name(), parameter.type());
        }

        analyzeStatement(method.body());

        endScope();
    }

    private void analyzeStatement(Statement statement) {

        if (statement instanceof Statement.Block block) {
            beginScope();

            for (Statement child : block.statements()) {
                analyzeStatement(child);
            }

            endScope();
            return;
        }

        if (statement instanceof Statement.VariableDeclaration declaration) {
            declare(declaration.name(), declaration.type());

            if (declaration.initializer() != null) {
                Type initializerType = analyzeExpression(
                        declaration.initializer()
                );

                checkAssignmentType(
                        declaration.type(),
                        initializerType,
                        declaration.name()
                );
            }

            return;
        }

        if (statement instanceof Statement.Assignment assignment) {
            Type variableType = lookup(assignment.name());

            Type valueType = analyzeExpression(assignment.value());

            checkAssignmentType(
                    variableType,
                    valueType,
                    assignment.name()
            );

            return;
        }

        if (statement instanceof Statement.ReturnStatement returnStatement) {
            if (returnStatement.value() != null) {
                analyzeExpression(returnStatement.value());
            }

            return;
        }

        if (statement instanceof Statement.Conditional conditional) {
            analyzeExpression(conditional.condition());
            analyzeStatement(conditional.thenBranch());

            if (conditional.elseBranch() != null) {
                analyzeStatement(conditional.elseBranch());
            }

            return;
        }

        if (statement instanceof Statement.WhileLoop loop) {
            analyzeExpression(loop.condition());
            analyzeStatement(loop.body());
            return;
        }

        if (statement instanceof Statement.ForLoop loop) {
            beginScope();

            if (loop.initializer() != null) {
                analyzeStatement(loop.initializer());
            }

            if (loop.condition() != null) {
                analyzeExpression(loop.condition());
            }

            if (loop.update() != null) {
                analyzeStatement(loop.update());
            }

            analyzeStatement(loop.body());

            endScope();
            return;
        }

        if (statement instanceof Statement.PrintStatement print) {
            for (Expression argument : print.arguments()) {
                analyzeExpression(argument);
            }

            return;
        }

        if (statement instanceof Statement.ExpressionStatement expressionStatement) {
            analyzeExpression(expressionStatement.expression());
        }
    }

    private Type analyzeExpression(Expression expression) {

        if (expression instanceof Expression.Literal literal) {
            return literal.type();
        }

        if (expression instanceof Expression.Variable variable) {
            return lookup(variable.name());
        }

        if (expression instanceof Expression.Binary binary) {
            Type leftType = analyzeExpression(binary.left());
            Type rightType = analyzeExpression(binary.right());

            return analyzeBinaryExpression(
                    binary.operator(),
                    leftType,
                    rightType
            );
        }

        if (expression instanceof Expression.FunctionCall functionCall) {
            for (Expression argument : functionCall.arguments()) {
                analyzeExpression(argument);
            }

            return Type.VOID;
        }

        throw new IllegalArgumentException(
                "Unknown expression type: " + expression
        );
    }

    private Type analyzeBinaryExpression(
            Expression.Operator operator,
            Type leftType,
            Type rightType
    ) {

        if (operator == Expression.Operator.GREATER_THAN
                || operator == Expression.Operator.LESS_THAN
                || operator == Expression.Operator.EQUAL) {

            return Type.INT;
        }

        if (leftType == Type.STRING || rightType == Type.STRING) {
            if (operator == Expression.Operator.PLUS
                    && leftType == Type.STRING
                    && rightType == Type.STRING) {
                return Type.STRING;
            }

            throw new IllegalArgumentException(
                    "Invalid arithmetic operation between "
                            + leftType + " and " + rightType
            );
        }

        if (!isNumeric(leftType) || !isNumeric(rightType)) {
            throw new IllegalArgumentException(
                    "Arithmetic operations require numeric operands."
            );
        }

        if (leftType == Type.DOUBLE || rightType == Type.DOUBLE) {
            return Type.DOUBLE;
        }

        return Type.INT;
    }

    private void checkAssignmentType(
            Type variableType,
            Type valueType,
            String variableName
    ) {

        if (variableType == valueType) {
            return;
        }

        if (variableType == Type.DOUBLE && valueType == Type.INT) {
            return;
        }

        throw new IllegalArgumentException(
                "Cannot assign " + valueType
                        + " to variable '" + variableName
                        + "' of type " + variableType
        );
    }

    private boolean isNumeric(Type type) {
        return type == Type.INT || type == Type.DOUBLE;
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(String name, Type type) {
        Map<String, Type> currentScope = scopes.peek();

        if (currentScope.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Variable '" + name + "' is already declared."
            );
        }

        currentScope.put(name, type);
    }

    private Type lookup(String name) {
        for (Map<String, Type> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }

        throw new IllegalArgumentException(
                "Variable '" + name + "' has not been declared."
        );
    }
}