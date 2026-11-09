package com.minicompiler.cli;
import com.minicompiler.ast.Program; import com.minicompiler.codegen.java.JavaCodeGenerator; import com.minicompiler.lexer.Lexer; import com.minicompiler.lexer.Token; import com.minicompiler.parser.Parser; import com.minicompiler.semantic.SemanticAnalyzer;
import java.io.IOException; import java.nio.file.Files; import java.nio.file.Path; import java.util.List;

public class Main {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println(
                    "Usage: java -jar mini-compiler.jar <source-file>"
            );
            System.exit(1);
        }

        try {
            compile(Path.of(args[0]));
        } catch (Exception exception) {
            System.err.println("Compilation failed:");
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    public static void compile(Path sourcePath) throws IOException {

        if (!Files.exists(sourcePath)) {
            throw new IllegalArgumentException(
                    "Source file not found: " + sourcePath
            );
        }

        if (!Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException(
                    "Source path is not a file: " + sourcePath
            );
        }

        String fileName = sourcePath.getFileName().toString();

        if (!fileName.endsWith(".khal")) {
            throw new IllegalArgumentException(
                    "Source files must have the .khal extension."
            );
        }

        String baseName = fileName.substring(
                0,
                fileName.length() - ".khal".length()
        );

        String className = baseName + "_kahl";

        String source = Files.readString(sourcePath);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Program program = parser.parseProgram();

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(program);

        JavaCodeGenerator generator = new JavaCodeGenerator();

        String generatedCode = generator.generate(
                program,
                className
        );

        Path outputPath = sourcePath.resolveSibling(
                className + ".java"
        );

        Files.writeString(outputPath, generatedCode);

        System.out.println("Compilation successful!");
        System.out.println(
                "Generated: " + outputPath.toAbsolutePath()
        );
    }


}