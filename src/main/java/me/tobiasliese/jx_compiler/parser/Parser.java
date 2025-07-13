package me.tobiasliese.jx_compiler.parser;

import me.tobiasliese.jxParser.JxParser;
import me.tobiasliese.jx_compiler.code_insight.TypeGraph;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Parser {

    private final Map<File, ParsedFile> files = new HashMap<>();

    public Map<File, ParsedFile> parseDirectory(File file) throws IOException, ClassNotFoundException {
        if (! file.isDirectory()) {
            files.put(file, parseFile(file));
        } else {
            for (File f: file.listFiles()) {
                files.putAll(parseDirectory(f));
            }
        }
        return Map.copyOf(files);
    }


    private ParsedFile parseFile(File path) throws IOException, ClassNotFoundException {
        CharStream charStream = CharStreams.fromFileName(path.getAbsolutePath());
        me.tobiasliese.jxParser.JxLexer lexer = new me.tobiasliese.jxParser.JxLexer(charStream);
        CommonTokenStream stream = new CommonTokenStream(lexer);
        var parser = new me.tobiasliese.jxParser.JxParser(stream);
        var component = parser.component();
        var imports = component.importDeclaration();
        var name = component.moduleDefinition().moduleName().Identifier().toString();
        var pck = component.packageDeclaration().identifier().stream()
                .map(me.tobiasliese.jxParser.JxParser.IdentifierContext::Identifier)
                .map(Objects::toString)
                .collect(Collectors.joining("."));
        var parameters = component.moduleDefinition().formalParameterList();
        var parameterGraph = parameterGraph(imports, parameters);

        return new ParsedFile(lexer, parser, name, pck, parameterGraph, component, parseImports(imports));
    }

    private List<String> parseImports(List<JxParser.ImportDeclarationContext> ctx) {
        return ctx.stream().map(this::parseImport).toList();
    }

    private String parseImport(JxParser.ImportDeclarationContext ctx) {
        return parseImport(ctx.singleTypeImportDeclaration().typeName().packageName());
    }

    private String parseImport(JxParser.PackageNameContext ctx) {
        if (ctx.packageName() == null) {
            return ctx.identifier().Identifier().toString();
        } else {
            return ctx.identifier().Identifier() + "." + parseImport(ctx.packageName());
        }
    }

    private TypeGraph parameterGraph(
            List<JxParser.ImportDeclarationContext> imports,
            JxParser.FormalParameterListContext params
    ) throws ClassNotFoundException {
        TypeGraph graph = new TypeGraph();

        if (params == null || params.children == null) {
            return graph;
        }

        for (var param: params.formalParameter()) {
            var name = param.variableDeclaratorId().getText();
            if (param.unannType().unannPrimitiveType() == null) {
                var t = param.unannType().unannReferenceType();
                if (t.unannArrayType() != null) {
                    var type = t.unannArrayType();
                } else if (t.unannTypeVariable() != null) {
                    var type = t.unannTypeVariable();
                    var desc = ClassDesc.of(getFullyQualifiedClassName(type.getText(), imports));
                    graph.addNode(name, desc);

                    Class.forName(getFullyQualifiedClassName(type.getText(), imports));
                    System.out.println("detected type " + type.getText());
                } else {
                    var type = t.unannClassOrInterfaceType();
                    graph.addNode(name, ClassDesc.of(getFullyQualifiedClassName(type.getText(), imports)));
                }
            } else {
                var type = param.unannType().unannPrimitiveType();
                String desc = switch (type.getText()) {
                    case "byte" ->
                            "B";
                    case "char" ->
                            "C";
                    case "double" ->
                            "D";
                    case "float" ->
                            "F";
                    case "int" ->
                            "I";
                    case "long" ->
                            "J";
                    case "short" ->
                            "S";
                    case "boolean" ->
                            "Z";
                    default ->
                            throw new RuntimeException("Unhandled primitive type");
                };
                graph.addNode(name, ClassDesc.ofDescriptor(desc));
            }
        }

        return graph;
    }

    private String getFullyQualifiedClassName(String type, List<JxParser.ImportDeclarationContext> imports) {
        var result = imports.stream()
                .map(o -> o.singleTypeImportDeclaration().typeName().getText())
                .filter(o -> o.endsWith(type))
                .findFirst();
        return result.orElseGet(() -> "java.lang." + type);
    }
}
