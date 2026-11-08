package com.minicompiler.ast;
import java.util.List;

public record Program(
        String name,
        List<String> imports,
        List<MethodDeclaration> methods,
        MethodDeclaration mainMethod
) {

    public Program {
        imports = List.copyOf(imports);
        methods = List.copyOf(methods);
    }

    public record MethodDeclaration(
            String name,
            List<Parameter> parameters,
            Type returnType,
            Statement.Block body
    ) {

        public MethodDeclaration {
            parameters = List.copyOf(parameters);
        }
    }

    public record Parameter(
            String name,
            Type type
    ) {}
}
