package me.tobiasliese.jx_tools;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JxTranspilerTest {
	private static final String stringEscapeSeq = "\"".repeat(3);


	@Test
	public void testExample() throws IOException {
		String sourceFileName = "example.jx";
		String sourcePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(sourceFileName)).getPath();
		String sourceContent = new String(Files.readAllBytes(Path.of(sourcePath)));

		String expectedResultName = "Example.java";
		String expectedResultPath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(expectedResultName)).getPath();
		String expectedResultContent = new String(Files.readAllBytes(Path.of(expectedResultPath)));

		JxTranspiler jxTranspiler = new JxTranspiler(stringEscapeSeq, stringEscapeSeq);
		assertEquals(expectedResultContent, jxTranspiler.transpile(sourceContent));
	}
}
