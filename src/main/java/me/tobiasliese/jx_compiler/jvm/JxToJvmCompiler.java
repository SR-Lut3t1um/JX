package me.tobiasliese.jx_compiler.jvm;


import me.tobiasliese.jxParser.JxParser;
import me.tobiasliese.jxParser.JxLexer;
import me.tobiasliese.jx_compiler.JxCompiler;
import me.tobiasliese.jx_compiler.jvm.code.TypeGraph;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.*;
import java.util.stream.Collectors;


/**
 * JxCompiler
 * compiles JX files to java bytecode
 *
 */
public class JxToJvmCompiler implements JxCompiler {
	private String name;

	/**
	 * Compiles a JX component into a Java value class files
	 * @param source
	 * @return
	 */
	@Override
	public byte[] compile(String path) throws IOException, ClassNotFoundException {
		CharStream charStream = CharStreams.fromFileName(path);
		var lexer = new JxLexer(charStream);
		CommonTokenStream stream = new CommonTokenStream(lexer);
		var parser = new JxParser(stream);
		var component = parser.component();
		var imports = component.importDeclaration();
		var name = component.moduleDefinition().moduleName().Identifier().toString();
        var pckg = component.packageDeclaration().identifier().stream()
                .map(JxParser.IdentifierContext::Identifier)
                .map(Objects::toString)
                .collect(Collectors.joining("."));
		var parameters = component.moduleDefinition().formalParameterList();
		var parameterGraph = parameterGraph(imports, parameters);

        ClassDesc desc = ClassDesc.of(pckg, name);
        ClassDesc stringDesc = ClassDesc.of("java.lang", "String");

        ClassDesc stringBuilder = ClassDesc.of("java.lang", "StringBuilder");
        MethodTypeDesc voidReturnType = MethodTypeDesc.ofDescriptor("()V");

        MethodTypeDesc renderMethodReturnType = MethodTypeDesc.of(stringDesc, parameterGraph.getNodes());

		return ClassFile.of().build(desc,
                clb -> clb.withFlags(ClassFile.ACC_PUBLIC)
                        .withMethodBody("<init>", voidReturnType, ClassFile.ACC_PUBLIC,
                                codeBuilder ->
                                        codeBuilder
                                                .aload(0)
                                                .invokespecial(ClassDesc.of("java.lang", "Object"), "<init>", voidReturnType)
                                                .return_()
                                )
                        .withMethodBody("render", renderMethodReturnType, ClassFile.ACC_PUBLIC,
                                codeBuilder -> {
                                    codeBuilder
                                            .new_(stringBuilder)
                                            .dup()
                                            .invokespecial(stringBuilder, "<init>", voidReturnType);

                                    JxImplementation impl = new JxImplementation(component.moduleDefinition().jxExpression(), codeBuilder, parameterGraph);
                                    impl.appender();

                                    codeBuilder
                                            .invokevirtual(stringBuilder, "toString", MethodTypeDesc.of(stringDesc))
                                            .areturn();
                                }));
	}


	private TypeGraph parameterGraph(
			List<JxParser.ImportDeclarationContext> imports,
			JxParser.FormalParameterListContext params
			) throws ClassNotFoundException {
        TypeGraph graph = new TypeGraph();

        if (params.children == null) {
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