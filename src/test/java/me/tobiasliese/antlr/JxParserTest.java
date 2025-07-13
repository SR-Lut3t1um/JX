package me.tobiasliese.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import me.tobiasliese.jxParser.JxParser;
import me.tobiasliese.jxParser.JxLexer;

import java.io.IOException;
import java.util.Objects;

public class JxParserTest {

    @Test
    void test() throws IOException {
        String sourceFile = "testcases/1/example.jx";
        String sourcePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(sourceFile)).getPath();

        CharStream charStream = CharStreams.fromFileName(sourcePath);
        var lexer = new JxLexer(charStream);
        CommonTokenStream stream = new CommonTokenStream(lexer);
        var parser = new JxParser(stream);
        var component = parser.component();
        System.out.println(component.packageDeclaration().identifier().getFirst().Identifier().toString());
        System.out.println(component.importDeclaration());
    }
}
