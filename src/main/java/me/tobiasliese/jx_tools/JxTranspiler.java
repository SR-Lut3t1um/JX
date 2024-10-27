package me.tobiasliese.jx_tools;

import org.parsers.java.*;
import org.parsers.java.ast.*;

import java.util.List;


public class JxTranspiler {
	JavaParser javaParser;
	private String prefix;
	private String suffix;
	private String name;
	int depth = 0;

	public JxTranspiler(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	StringBuilder transpile(List<Node> nodes, StringBuilder builder) {
		nodes.forEach(c -> transpile(c, builder));
		return builder;
	}

	/**
	 *
	 * since children is an unmodifiable list, we need to be able to provide a different list
	 * @param expression
	 * @param children
	 * @param builder
	 * @return
	 */
	StringBuilder transpile(JxExpression expression, List<Node> children, StringBuilder builder) {
		if (expression == null)
			return builder;
		if (depth++ == 0) {
			builder.append(prefix);
		}
		transpile(children, builder);
		if (--depth == 0) {
			builder.append(suffix);
		}
		return builder;
	}

	StringBuilder transpile(JxExpression expression, StringBuilder builder) {
		transpile(expression, expression.children(), builder);
		return builder.append(System.lineSeparator());
	}

	StringBuilder transpile(JxFragment jxFragment, StringBuilder builder) {
		// drop the '<>' and </>
		List<Node> children = jxFragment.children().subList(2,  jxFragment.children().size() - 3);

		transpile(jxFragment, children, builder);
		return builder;
	}

	/**
	 * for the transpiler we want to include all the
	 * @param node
	 * @param builder
	 * @return
	 */
	StringBuilder transpile(Node node, StringBuilder builder) {
		switch (node) {
			case JxFragment jxFragment ->
				transpile(jxFragment, builder);
			case JxElement jxElement ->
				transpile(jxElement, builder);
			case Identifier identifier when node.getParent().getClass().equals(ClassDeclaration.class) -> {
				name = identifier.toString();
				builder.append(name);
				transpile(node.children(), builder);
			}
			case Node.TerminalNode terminalNode -> {
				builder.append(" ");
				builder.append(terminalNode.toString());
			}
			case JxChild jxChild -> {
				builder.append("<slot>");
				transpile(jxChild.children(), builder);
				builder.append("</slot>");
			}
			default -> transpile(node.children(), builder);
		}
		return builder;
	}

	/**
	 *
	 * @param source
	 * @return
	 */
	String transpile(String source) {
		javaParser = new JavaParser(source);

		var root = javaParser.Root();
		StringBuilder builder = new StringBuilder();

		return transpile(root, builder).toString();
	}

	public String getName() {
		return name;
	}
}
