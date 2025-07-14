package me.tobiasliese.jx_compiler;

import me.tobiasliese.jx_compiler.jvm.JxToJvmCompiler;
import me.tobiasliese.jx_compiler.parser.ParsedFile;
import me.tobiasliese.jx_compiler.parser.Parser;
import me.tobiasliese.jx_compiler.util.TopologicalSorting;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Compiler {
    public void compilePath(File file) throws IOException, ClassNotFoundException {
        var compiler = new JxToJvmCompiler();
        Parser parser = new Parser();
        var parsed = parser.parseDirectory(file);
        var dependencies = figureOutDependencies(parsed);

        var compileOrder = figureOutCompileOrder(dependencies);
        System.out.println(compileOrder);
        for (var workSet: compileOrder) {
            for (var work: workSet) {
                compiler.compile(parsed.get(work));
            }
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
        worksets.add(TopologicalSorting.kahnsAlgorithm(dependencies));
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
