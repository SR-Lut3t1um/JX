package me.tobiasliese.jx_compiler;

public class CompilerClassLoader extends ClassLoader {
    public Class<?> define(String name, byte[] bytecode) {
        return defineClass(name, bytecode, 0, bytecode.length);
    }
}