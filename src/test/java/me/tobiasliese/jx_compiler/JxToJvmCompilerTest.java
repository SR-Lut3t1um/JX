package me.tobiasliese.jx_compiler;

import me.tobiasliese.jx_compiler.jvm.JxToJvmCompiler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class JxToJvmCompilerTest {
    @Test
    void simpleTest() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JxToJvmCompiler compiler = new JxToJvmCompiler();
        String sourceFileName = "example.jx";
        String sourcePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(sourceFileName)).getPath();
        var clazz = compiler.compile(sourcePath);

        ClassModel cm = ClassFile.of().parse(clazz);
        Class<?> cls = new ClassLoader() {
            public Class<?> define() {
                return defineClass("example.example.Example", clazz, 0, clazz.length);
            }
        }.define();

        Object instance = cls.getDeclaredConstructor().newInstance();
        Method method = cls.getDeclaredMethod("render", String.class, float.class, me.tobiasliese.jx_compiler.jvm.Test.class);
        String result = (String) method.invoke(instance, "Hello world", 1.0f, new me.tobiasliese.jx_compiler.jvm.Test("sdf", 1));

        System.out.println(result);
    }
}
