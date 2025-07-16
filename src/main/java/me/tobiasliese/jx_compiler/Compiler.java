package me.tobiasliese.jx_compiler;

import me.tobiasliese.jx_compiler.jvm.JxToJvmCompiler;
import me.tobiasliese.jx_compiler.parser.ParsedFile;
import me.tobiasliese.jx_compiler.parser.ParserUtil;
import me.tobiasliese.jx_compiler.util.TopologicalSorting;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Compiler {
    public void compilePath(File source, File target) throws IOException, ClassNotFoundException {
        var compiler = new JxToJvmCompiler();
        ParserUtil parser = new ParserUtil();
        var parsed = parser.parseDirectory(source);
        var dependencies = figureOutDependencies(parsed);
        CompilerClassLoader cmp = new CompilerClassLoader();

        var compileOrder = figureOutCompileOrder(dependencies);
        for (var workSet: compileOrder) {
            for (var work: workSet) {
                Path output = target
                        .toPath()
                        .toAbsolutePath()
                        .resolve(
                                source
                                        .toPath()
                                        .relativize(work.toPath())
                        )
                        .getParent();
                Files.createDirectories(output);
                output = output.resolve(parsed.get(work).name() + ".class");
                byte[] clazz = compiler.compile(parsed.get(work), cmp);
                cmp.define(
                        parsed.get(work).pck() + "." + parsed.get(work).name(),
                        clazz
                );
                writeFile(output.toFile(), clazz);
            }
        }
    }

    private void writeFile(File file, byte[] content) throws IOException {
        try (var writer = new FileOutputStream(file)) {
            writer.write(content);
        }
    }

    /**
     * Figures out the order in which the files need to be compiled.
     *
     * @param dependencies A map of file dependencies
     * @throws IllegalStateException in case there is a cyclic dependency
     * @return A list l of lists w where l is independent and w is ordered
     */
    private List<List<File>> figureOutCompileOrder(Map<File, List<File>> dependencies) throws IllegalStateException {
        // todo implement parallel algorithm
        List<List<File>> worksets = new ArrayList<>();
        worksets.add(TopologicalSorting.kahnsAlgorithm(dependencies).reversed());
        return worksets;
    }

    private Map<File, List<File>> figureOutDependencies(Map<File, ParsedFile> parsedFiles) {
        ConcurrentMap<File, List<File>> dependencies = new ConcurrentHashMap<>();
        parsedFiles.entrySet().stream().parallel().forEach(entry -> {

            var file = entry.getKey();
            var parsedFile = entry.getValue();
            dependencies.putIfAbsent(file, new ArrayList<>());
            parsedFile.imports().forEach(imp -> {
                parsedFiles.forEach((key2, value2) -> {
                    if (imp.equals(value2.pck() + "." + value2.name())) {
                        dependencies.get(file).add(key2);
                    }
                });
            });
        });
        return dependencies;
    }
}
