package me.tobiasliese.jx_compiler;

import me.tobiasliese.jxParser.JxParser;
import me.tobiasliese.jx_compiler.jvm.JxToJvmCompiler;
import me.tobiasliese.jx_compiler.parser.ParsedFile;
import me.tobiasliese.jx_compiler.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Compiler {

    public void compilePath(File file) throws IOException, ClassNotFoundException {
        var compiler = new JxToJvmCompiler();
        Parser parser = new Parser();
        var parsed = parser.parseDirectory(file);
        var dependencies = figureOutDependencies(parsed);
        for (var entry: parsed.entrySet()) {
            if (dependencies.get(entry.getKey()) == null) {
                compiler.compile(entry.getValue());
            }
        }
    }

    private Map<File, List<File>> figureOutDependencies(Map<File, ParsedFile> parsedFiles) {
        Map<File, List<File>> dependencies = new HashMap<>();
        parsedFiles.forEach((key, value) -> {
            value.imports().forEach(imp -> {
                parsedFiles.forEach((key2, value2) -> {
                    if (imp.equals(value2.pck() + "." + value2.name())) {
                        dependencies.putIfAbsent(key, new ArrayList<>());
                        dependencies.get(key).add(key2);
                    }
                });
            });
        });
        return dependencies;
    }
}
