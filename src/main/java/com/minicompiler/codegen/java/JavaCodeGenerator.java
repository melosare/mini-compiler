package com.minicompiler.codegen.java;
import com.minicompiler.ast.Expression;
import com.minicompiler.ast.Program;
import com.minicompiler.ast.Statement;
import com.minicompiler.ast.Type;

import java.util.List;

public class JavaCodeGenerator {

    private final StringBuilder output = new StringBuilder();
    private int indentation = 0;

    public String generate(Program program) {
        output.setLength(0);
        indentation = 0;

        output.append("public class ")
                .append(program.name())
                .append(" {\n");

        indentation++;

        for (Program.MethodDeclaration method : program.methods()) {
            generateMethod(method);
        }

        generateMain(program.mainMethod());

        indentation--;

        output.append("}\n");

        return output.toString();
    }

    private void generateMethod(Program.MethodDeclaration method) {
        indent();

        output.append(javaType(method.returnType()))
                .append(" ")
                .append(method.name())
                .append("(");

        generateParameters(method.parameters());

        output.append(") {\n");

        indentation++;
        generateStatement(method.body());
        indentation--;

        indent();
        output.append("}\n\n");
    }

    private void generateMain(Program.MethodDeclaration method) {
        indent();

        output.append("public static void main(String[] args) {\n");

        indentation++;
        generateStatement(method.body());
        indentation--;

        indent();
        output.append("}\n");
    }

    private void generateParameters(
            List<Program.Parameter> parameters
    ) {
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                output.append(", ");
            }

            Program.Parameter parameter = parameters.get(i);

            output.append(javaType(parameter.type()))
                    .append(" ")
                    .append(parameter.name());
        }
    }
    private void generateStatement(Statement statement) {

        if (statement instanceof Statement.Block block) {
            for (Statement child : block.statements()) {
                generateStatement(child);
            }
            return;
        }

        if (statement instanceof Statement.VariableDeclaration declaration) {
            indent();
            output.append(javaType(declaration.type()))
                    .append(" ")
                    .append(declaration.name());

            if (declaration.initializer() != null) {
                output.append(" = ");
                generateExpression(declaration.initializer());
            }

            output.append(";\n");
            return;
        }

        if (statement instanceof Statement.Assignment assignment) {
            indent();
            output.append(assignment.name()).append(" = ");
            generateExpression(assignment.value());
            output.append(";\n");
            return;
        }

        if (statement instanceof Statement.ReturnStatement returnStatement) {
            indent();
            output.append("return");

            if (returnStatement.value() != null) {
                output.append(" ");
                generateExpression(returnStatement.value());
            }

            output.append(";\n");
            return;
        }

        if (statement instanceof Statement.PrintStatement print) {
            indent();

            output.append(
                    print.newline()
                            ? "System.out.println("
                            : "System.out.print("
            );

            for (int i = 0; i < print.arguments().size(); i++) {
                if (i > 0) {
                    output.append(" + ");
                }

                generateExpression(print.arguments().get(i));
            }

            output.append(");\n");
            return;
        }

        if (statement instanceof Statement.Conditional conditional) {
            indent();
            output.append("if (");
            generateExpression(conditional.condition());
            output.append(") {\n");

            indentation++;
            generateStatement(conditional.thenBranch());
            indentation--;

            indent();
            output.append("}");

            if (conditional.elseBranch() != null) {
                output.append(" else {\n");

                indentation++;
                generateStatement(conditional.elseBranch());
                indentation--;

                indent();
                output.append("}");
            }

            output.append("\n");
            return;
        }

        if (statement instanceof Statement.WhileLoop loop) {
            indent();
            output.append("while (");
            generateExpression(loop.condition());
            output.append(") {\n");

            indentation++;
            generateStatement(loop.body());
            indentation--;

            indent();
            output.append("}\n");
            return;
        }

        if (statement instanceof Statement.ExpressionStatement expressionStatement) {
            indent();
            generateExpression(expressionStatement.expression());
            output.append(";\n");
        }
    }

    private void generateExpression(Expression expression) {

        if (expression instanceof Expression.Literal literal) {
            output.append(literal.value());
            return;
        }

        if (expression instanceof Expression.Variable variable) {
            output.append(variable.name());
            return;
        }

        if (expression instanceof Expression.Binary binary) {
            output.append("(");
            generateExpression(binary.left());

            output.append(" ")
                    .append(operator(binary.operator()))
                    .append(" ");

            generateExpression(binary.right());
            output.append(")");
            return;
        }

        if (expression instanceof Expression.FunctionCall call) {
            output.append(call.name()).append("(");

            for (int i = 0; i < call.arguments().size(); i++) {
                if (i > 0) {
                    output.append(", ");
                }

                generateExpression(call.arguments().get(i));
            }

            output.append(")");
        }
    }

    private String operator(Expression.Operator operator) {
        return switch (operator) {
            case PLUS -> "+";
            case MINUS -> "-";
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
            case GREATER_THAN -> ">";
            case LESS_THAN -> "<";
            case EQUAL -> "==";
        };
    }

    private String javaType(Type type) {
        return switch (type) {
            case INT -> "int";
            case DOUBLE -> "double";
            case STRING -> "String";
            case BOOLEAN -> "boolean";
            case VOID -> "void";
        };
    }

    private void indent() {
        output.append("    ".repeat(indentation));
    }
}
