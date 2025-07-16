package me.tobiasliese.jx_compiler.jvm;


import io.github.treesitter.jtreesitter.Node;
import io.github.treesitter.jtreesitter.Tree;
import me.tobiasliese.jx_compiler.code_insight.TypeGraph;
import me.tobiasliese.jx_compiler.parser.ParsedFile;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class JxImplementation {
    CodeBuilder codeBuilder;
    TypeGraph typeGraph;
    ClassLoader classLoader;
    ParsedFile parsedFile;


    ClassDesc STRING_BUILDER = ClassDesc.of("java.lang", "StringBuilder");
    ClassDesc STRING = ClassDesc.of("java.lang.String");

    public JxImplementation(
            CodeBuilder codeBuilder,
            TypeGraph typeGraph,
            ClassLoader cl,
            ParsedFile parsedFile
    ) {
        this.codeBuilder = codeBuilder;
        this.typeGraph = typeGraph;
        this.classLoader = cl;
        this.parsedFile = parsedFile;
    }

    public void appender() throws ClassNotFoundException {
//        walkTree(type);
    }

    void walkTree(Node node) throws ClassNotFoundException {
//        switch (node.getType()) {
//            case "" -> {
//                loadVar(ctx);
//                appendStringBuilder(ctx);
//            }
//            case TerminalNode t
//                    when ! t.getParent().getClass().equals(JxParser.JxChildContext.class) -> {
//                codeBuilder
//                        .ldc(t.getText())
//                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
//            }
//            case JxParser.JxOpeningElementContext ctx -> {
//                // append input of element name
//                codeBuilder
//                        .ldc(ctx.getText())
//                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
//            }
//            case JxParser.HtmlGlobalAttributesContext ctx -> {
//                codeBuilder
//                        .ldc(" ")
//                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
//                for (int i = 0; i < node.getChildCount(); i++) {
//                    walkTree(node.getChild(i));
//                }
//            }
//            case "" -> {
//                String className = ctx.jxElementName().jxIdentifier().Identifier().toString();
//                Optional<String> classPath = parsedFile.imports().stream().filter(s -> s.endsWith(className)).findFirst();
//                if (classPath.isEmpty()) {
//                    throw new IllegalStateException("Missing import for component: " + className);
//                }
//
//                Class<?> clazz = classLoader.loadClass(classPath.get());
//                // todo run render method
//                var method = Arrays.stream(clazz.getDeclaredMethods())
//                        .filter(elem -> elem.getName().equals("render"))
//                        .findFirst();
//                if (method.isEmpty()) {
//                    throw new IllegalStateException("Component has no render method");
//                }
//
//                for (var param: method.get().getParameters()) {
//                }
//                // todo profit
//            }
//            case JxParser.JxClosingElementContext ctx -> {
//                // do nothing
//            }
//            case JxParser.JxIdentifierContext ctx
//                    when ! ctx.children.getFirst().getClass().equals(JxParser.HtmlElementContext.class) -> {
//                codeBuilder
//                        .ldc(ctx.getText())
//                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
//            }
//            default -> {
//                for (Node n: node.getChildren()) {
//                    walkTree(n);
//                }
//            }
//        }
    }

    private void loadComponent(String name, String importPath) {
        ClassDesc.of(importPath, name);
    }

//    private void loadVar(JxParser.PostfixExpressionContext ctx) {
//        String identifier;
//        if (ctx.expressionName() != null) {
//            identifier = ctx.expressionName().identifier().Identifier().toString();
//        } else {
//            identifier = ctx.primary().primaryNoNewArray().typeName().packageName().identifier().Identifier().toString();
//        }
//
//        var desc = typeGraph.getNode(identifier);
//        var paramIndex = typeGraph.getNodeIndex(identifier) + 1;
//
//        if (desc.isPrimitive()) {
//            switch (desc.descriptorString()) {
//                case "F" ->
//                        codeBuilder
//                                .fload(paramIndex);
//                case "I" ->
//                        codeBuilder
//                                .iload(paramIndex);
//            }
//        } else {
//            codeBuilder
//                    .aload(paramIndex);
//        }
//        desc = walkCall(ctx, desc);
//
//        convertToString(desc);
//    }

//    private ClassDesc walkCall(JxParser.PostfixExpressionContext ctx, ClassDesc desc) {
//        String methodOrField;
//        try {
//             methodOrField = ctx.primary().primaryNoNewArray().identifier().Identifier().toString();
//        } catch (NullPointerException ignore) {
//            return desc;
//        }
//
//        if ( // I know this is hideous
//                ctx.primary().primaryNoNewArray().children.get(3).getText().equals("(") &&
//                        ctx.primary().primaryNoNewArray().children.get(4).getText().equals(")")) {
//            var cm = TypeGraph.getCodeModel(desc);
//            var methodDesc = cm.getMethods().get(methodOrField);
//            codeBuilder
//                    .invokevirtual(desc, methodOrField, methodDesc);
//            desc = methodDesc.returnType();
//        } else {
//            var cm = TypeGraph.getCodeModel(desc);
//            var fieldDesc = cm.getFields().get(methodOrField);
//            codeBuilder.getfield(desc, methodOrField, fieldDesc);
//            desc = fieldDesc;
//        }
//        return walkCall(ctx.primary().primaryNoNewArray().pNNA(), desc);
//    }
//
//    private ClassDesc walkCall(JxParser.PNNAContext ctx, ClassDesc desc) {
//        if (ctx == null) {
//            return desc;
//        }
//
//        var methodOrField = ctx.identifier().Identifier().toString();
//        boolean isMethod = ctx.children.get(2).getText().equals("(");
//        var cm = TypeGraph.getCodeModel(desc);
//        if (isMethod) {
//            var methodDesc = cm.getMethods().get(methodOrField);
//            if (methodDesc == null) {
//                throw new RuntimeException("Method does not exist");
//            }
//            codeBuilder
//                    .invokevirtual(desc, methodOrField, methodDesc);
//            desc = methodDesc.returnType();
//        } else {
//            var fieldDesc = cm.getFields().get(methodOrField);
//            codeBuilder.getfield(desc, methodOrField, fieldDesc);
//            desc = fieldDesc;
//        }
//        return walkCall(ctx.pNNA(), desc);
//    }

    private List<ClassDesc> valueOfDescs() {
        List<ClassDesc> list = new ArrayList<>();
        list.add(ClassDesc.ofDescriptor(char.class.descriptorString()));
        list.add(ClassDesc.ofDescriptor(double.class.descriptorString()));
        list.add(ClassDesc.ofDescriptor(float.class.descriptorString()));
        list.add(ClassDesc.ofDescriptor(int.class.descriptorString()));
        list.add(ClassDesc.ofDescriptor(long.class.descriptorString()));
        list.add(ClassDesc.ofDescriptor(short.class.descriptorString()));
        list.add(ClassDesc.ofDescriptor("[C"));
        return list;
    }

    private void convertToString(ClassDesc desc) {
        if (desc.equals(STRING))
            return;

        if (valueOfDescs().contains(desc)) {
            codeBuilder
                    .invokestatic(
                            STRING,
                            "valueOf",
                            MethodTypeDesc.of(
                                    STRING,
                                    desc
                            )
                    );
        } else {
            codeBuilder.invokevirtual(
                    desc,
                    "toString",
                    MethodTypeDesc.of(
                            STRING
                    )
            );
        }
    }

//    private void appendStringBuilder(JxParser.PostfixExpressionContext ctx) {
//        codeBuilder
//                .invokevirtual(
//                        STRING_BUILDER,
//                        "append",
//                        MethodTypeDesc.of(STRING_BUILDER,ClassDesc.of("java.lang", "String"))
//                );
//    }
}