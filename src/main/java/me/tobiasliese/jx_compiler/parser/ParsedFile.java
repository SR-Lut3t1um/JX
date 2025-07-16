package me.tobiasliese.jx_compiler.parser;

import me.tobiasliese.jx_compiler.code_insight.TypeGraph;

import java.util.List;

public record ParsedFile(
        String name,
        String pck,
        TypeGraph typeGraph,
        List<String> imports
) {
}
