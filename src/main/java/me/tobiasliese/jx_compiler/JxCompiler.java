package me.tobiasliese.jx_compiler;


import me.tobiasliese.jx_compiler.parser.ParsedFile;

import java.io.IOException;

public interface JxCompiler {
    byte[] compile(ParsedFile file) throws IOException, ClassNotFoundException;
}
