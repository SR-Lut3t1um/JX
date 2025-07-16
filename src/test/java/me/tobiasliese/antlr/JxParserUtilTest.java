package me.tobiasliese.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

public class JxParserUtilTest {

    @Test
    void test() throws IOException {
        String sourceFile = "testcases/1/example.jx";
        String sourcePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(sourceFile)).getPath();



    }
}
