package me.tobiasliese.jx_compiler.jvm;


import me.tobiasliese.jx_compiler.JxCompiler;
import me.tobiasliese.jx_compiler.parser.ParsedFile;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.attribute.MethodParameterInfo;
import java.lang.classfile.attribute.MethodParametersAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * JxCompiler
 * compiles JX files to java bytecode
 *
 */
public class JxToJvmCompiler implements JxCompiler {

	/**
	 * Compiles a JX component into a Java value class files
	 * @param source
	 * @return
	 */
	@Override
	public byte[] compile(ParsedFile file, ClassLoader csl) throws IOException, ClassNotFoundException {

        ClassDesc desc = ClassDesc.of(file.pck(), file.name());
        ClassDesc stringDesc = ClassDesc.of("java.lang", "String");

        ClassDesc stringBuilder = ClassDesc.of("java.lang", "StringBuilder");
        MethodTypeDesc voidReturnType = MethodTypeDesc.ofDescriptor("()V");

        MethodTypeDesc renderMethodReturnType = MethodTypeDesc.of(stringDesc, file.typeGraph().getNodes());
        List<String> parameterNames = file.typeGraph().getNodeNames();
        List<MethodParameterInfo> attrs = new ArrayList<>();
        for (var name: parameterNames) {
            attrs.add(MethodParameterInfo.of(Optional.of(name)));
        }

		return ClassFile.of().build(desc,
                clb -> clb.withFlags(ClassFile.ACC_PUBLIC)
                        .withMethodBody("<init>", voidReturnType, ClassFile.ACC_PUBLIC,
                                codeBuilder ->
                                        codeBuilder
                                                .aload(0)
                                                .invokespecial(ClassDesc.of("java.lang", "Object"), "<init>", voidReturnType)
                                                .return_()
                                )
                        .withMethod("render", renderMethodReturnType, ClassFile.ACC_PUBLIC,
                                mb -> {
                                    mb
                                            .with(
                                                MethodParametersAttribute.of(attrs)
                                            )
                                            .withCode(
                                            codeBuilder -> {
                                                codeBuilder
                                                        .new_(stringBuilder)
                                                        .dup()
                                                        .invokespecial(stringBuilder, "<init>", voidReturnType);

//                                                JxImplementation impl = new JxImplementation(file.componentContext().moduleDefinition().jxExpression(), codeBuilder, file.typeGraph(), csl, file);
//                                                try {
//                                                    impl.appender();
//                                                } catch (ClassNotFoundException e) {
//                                                    throw new RuntimeException(e);
//                                                }

                                                codeBuilder
                                                        .invokevirtual(stringBuilder, "toString", MethodTypeDesc.of(stringDesc))
                                                        .areturn();
                                            }
                                    );
                                }
                                ));
	}
}