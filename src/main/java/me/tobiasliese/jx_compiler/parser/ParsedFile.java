package me.tobiasliese.jx_compiler.parser;

import me.tobiasliese.jxParser.JxParser;
import me.tobiasliese.jx_compiler.code_insight.TypeGraph;

import java.util.List;

public record ParsedFile(
        me.tobiasliese.jxParser.JxLexer lexer,
        me.tobiasliese.jxParser.JxParser parser,
        String name,
        String pck,
        TypeGraph parameterGraph,
        JxParser.ComponentContext componentContext,
        List<String> imports
) {
}
