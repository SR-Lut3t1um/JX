package me.tobiasliese.jx_compiler.jvm;


import me.tobiasliese.jxParser.JxParser;
import me.tobiasliese.jx_compiler.code_insight.TypeGraph;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;

class JxImplementation {
    JxParser.JxExpressionContext jxExpressionContext;
    CodeBuilder codeBuilder;
    TypeGraph typeGraph;


    ClassDesc STRING_BUILDER = ClassDesc.of("java.lang", "StringBuilder");
    ClassDesc STRING = ClassDesc.of("java.lang.String");

    public JxImplementation(JxParser.JxExpressionContext jxExpressionContext, CodeBuilder codeBuilder, TypeGraph typeGraph) {
        this.jxExpressionContext = jxExpressionContext;
        this.codeBuilder = codeBuilder;
        this.typeGraph = typeGraph;
    }

    public void appender() {
        var type = jxExpressionContext.children.getFirst();
        walkTree(type);
    }

    void walkTree(ParseTree tree) {
        switch (tree) {
            case JxParser.PostfixExpressionContext ctx -> {
                loadVar(ctx);
                appendStringBuilder(ctx);
            }
            case TerminalNode t
                    when ! t.getParent().getClass().equals(JxParser.JxChildContext.class) -> {
                codeBuilder
                        .ldc(t.getText())
                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            }
            case JxParser.JxOpeningElementContext ctx -> {
                // append input of element name
                System.out.println(ctx.jxElementName().jxIdentifier().Identifier());
                codeBuilder
                        .ldc(ctx.getText())
                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            }
            case JxParser.HtmlGlobalAttributesContext ctx -> {
                codeBuilder
                        .ldc(" ")
                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
                for (int i = 0; i < tree.getChildCount(); i++) {
                    walkTree(tree.getChild(i));
                }
            }
            case JxParser.JxSelfClosingElementContext ctx -> {
                // todo load
                // todo run render method
                // todo profit
                System.out.println(ctx.jxElementName().jxIdentifier().Identifier());
            }
            case JxParser.JxClosingElementContext ctx -> {
                // do nothing
            }
            case JxParser.JxIdentifierContext ctx
                    when ! ctx.children.getFirst().getClass().equals(JxParser.HtmlElementContext.class) -> {
                codeBuilder
                        .ldc(ctx.getText())
                        .invokevirtual(STRING_BUILDER, "append", MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            }
            default -> {
                for (int i = 0; i < tree.getChildCount(); i++) {
                    walkTree(tree.getChild(i));
                }
            }
        }
    }

    private void loadComponent(String name, String importPath) {
        ClassDesc.of(importPath, name);
    }

    private void loadVar(JxParser.PostfixExpressionContext ctx) {
        String identifier;
        if (ctx.expressionName() != null) {
            identifier = ctx.expressionName().identifier().Identifier().toString();
        } else {
            identifier = ctx.primary().primaryNoNewArray().typeName().packageName().identifier().Identifier().toString();
        }

        var desc = typeGraph.getNode(identifier);
        var paramIndex = typeGraph.getNodeIndex(identifier) + 1;

        if (desc.isPrimitive()) {
            switch (desc.descriptorString()) {
                case "F" ->
                        codeBuilder
                                .fload(paramIndex);
                case "I" ->
                        codeBuilder
                                .iload(paramIndex);
            }
        } else {
            codeBuilder
                    .aload(paramIndex);
        }
        desc = walkCall(ctx, desc);

        convertToString(desc);
    }

    private ClassDesc walkCall(JxParser.PostfixExpressionContext ctx, ClassDesc desc) {
        String methodOrField;
        try {
             methodOrField = ctx.primary().primaryNoNewArray().identifier().Identifier().toString();
        } catch (NullPointerException ignore) {
            return desc;
        }

        if ( // I know this is hideous
                ctx.primary().primaryNoNewArray().children.get(3).getText().equals("(") &&
                        ctx.primary().primaryNoNewArray().children.get(4).getText().equals(")")) {
            var cm = TypeGraph.getCodeModel(desc);
            var methodDesc = cm.getMethods().get(methodOrField);
            codeBuilder
                    .invokevirtual(desc, methodOrField, methodDesc);
            desc = methodDesc.returnType();
        } else {
            var cm = TypeGraph.getCodeModel(desc);
            var fieldDesc = cm.getFields().get(methodOrField);
            codeBuilder.getfield(desc, methodOrField, fieldDesc);
            desc = fieldDesc;
        }
        return walkCall(ctx.primary().primaryNoNewArray().pNNA(), desc);
    }

    private ClassDesc walkCall(JxParser.PNNAContext ctx, ClassDesc desc) {
        if (ctx == null) {
            return desc;
        }

        var methodOrField = ctx.identifier().Identifier().toString();
        boolean isMethod = ctx.children.get(2).getText().equals("(");
        var cm = TypeGraph.getCodeModel(desc);
        if (isMethod) {
            var methodDesc = cm.getMethods().get(methodOrField);
            if (methodDesc == null) {
                throw new RuntimeException("Method does not exist");
            }
            codeBuilder
                    .invokevirtual(desc, methodOrField, methodDesc);
            System.out.println(methodDesc.returnType());
            desc = methodDesc.returnType();
        } else {
            var fieldDesc = cm.getFields().get(methodOrField);
            codeBuilder.getfield(desc, methodOrField, fieldDesc);
            System.out.println(fieldDesc);
            desc = fieldDesc;
        }
        return walkCall(ctx.pNNA(), desc);
    }

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

    private void appendStringBuilder(JxParser.PostfixExpressionContext ctx) {
        codeBuilder
                .invokevirtual(
                        STRING_BUILDER,
                        "append",
                        MethodTypeDesc.of(STRING_BUILDER,ClassDesc.of("java.lang", "String"))
                );
    }
}