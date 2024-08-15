package me.tobiasliese;

import org.parsers.java.JavaParser;
import org.parsers.java.ast.PrimaryExpression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	public static void main(String[] args) throws IOException {
		Path path = Path.of("/home/tliese/JX/src/main/resources/example.jx");

		var parser = new JavaParser(String.join("\n", Files.readAllLines(path)));
		var root = parser.Root();
		root.childrenOfType(PrimaryExpression.class).forEach(System.out::println);
		parser.rootNode().children(false).stream().forEach(System.out::println);
	}
}
