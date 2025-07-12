package me.tobiasliese.jx_compiler;


import java.io.IOException;

public interface JxCompiler {
    byte[] compile(String path) throws IOException, ClassNotFoundException;
}
