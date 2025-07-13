package me.tobiasliese.jx_compiler.jvm;


import me.tobiasliese.jx_compiler.JxCompiler;
import me.tobiasliese.jx_compiler.parser.ParsedFile;

import java.io.File;
import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;


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
	public byte[] compile(ParsedFile file) throws IOException, ClassNotFoundException {

        ClassDesc desc = ClassDesc.of(file.pck(), file.name());
        ClassDesc stringDesc = ClassDesc.of("java.lang", "String");

        ClassDesc stringBuilder = ClassDesc.of("java.lang", "StringBuilder");
        MethodTypeDesc voidReturnType = MethodTypeDesc.ofDescriptor("()V");

        MethodTypeDesc renderMethodReturnType = MethodTypeDesc.of(stringDesc, file.parameterGraph().getNodes());

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

                                    JxImplementation impl = new JxImplementation(file.componentContext().moduleDefinition().jxExpression(), codeBuilder, file.parameterGraph());
                                    impl.appender();

                                    codeBuilder
                                            .invokevirtual(stringBuilder, "toString", MethodTypeDesc.of(stringDesc))
                                            .areturn();
                                }));
	}
}