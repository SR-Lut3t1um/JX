package me.tobiasliese.jx_compiler;

import me.tobiasliese.jx_compiler.parser.ParsedFile;
import me.tobiasliese.jx_compiler.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class Compiler {
    public void compilePath(File file) throws IOException, ClassNotFoundException {
        Parser parser = new Parser();
        var parsed = parser.parseDirectory(file);
        figureOutDependencies(parsed);
    }

    private void figureOutDependencies(Map<File, ParsedFile> parsedFiles) {
        parsedFiles.entrySet().forEach(entry -> {
            System.out.println(entry.getValue().imports());
        });
    }
}
