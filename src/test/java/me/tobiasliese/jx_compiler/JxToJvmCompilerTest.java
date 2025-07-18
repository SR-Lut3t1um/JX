package me.tobiasliese.jx_compiler;

import me.tobiasliese.jx_compiler.jvm.JxToJvmCompiler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class JxToJvmCompilerTest {
    @Test
    void simpleTest() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Compiler compiler = new Compiler();
        String sourcePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("testcases/1")).getPath();
        String targetPath = "target/test-output/test1";
        compiler.compilePath(new File(sourcePath), new File(targetPath));


//
//        Object instance = cls.getDeclaredConstructor().newInstance();
//        Method method = cls.getDeclaredMethod("render", String.class, float.class, me.tobiasliese.jx_compiler.jvm.Test.class);
//        String result = (String) method.invoke(instance, "Hello world", 1.0f, new me.tobiasliese.jx_compiler.jvm.Test("sdf", 1));
//
//        System.out.println(result);
    }
}
