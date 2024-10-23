package me.tobiasliese.jx_tools;

import org.parsers.java.JavaParser;

public class JxParser {
	private JavaParser javaParser;

	JxParser(String content) {
		javaParser = new JavaParser(content);
		javaParser.setParserTolerant(true);
	}

	String types() {
		return "";
	}
}
