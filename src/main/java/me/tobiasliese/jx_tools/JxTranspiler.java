package me.tobiasliese.jx_tools;

import org.parsers.java.*;
import org.parsers.java.ast.*;

import java.util.List;


public class JxTranspiler {
	JavaParser javaParser;
	private static final String stringEscapeSeq = "\"".repeat(3);

	boolean isJxElement(Node node) {
		return node.getClass().equals(JxElement.class) || node.getClass().equals(JxFragment.class);
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
		if (!isJxElement(expression.getParent())) {
			builder.append(stringEscapeSeq);
		}
		transpile(children, builder);
		if (children.getFirst() != null && !isJxElement(children.getFirst())) {
			builder.append(stringEscapeSeq);
		}
		return builder;
	}

	StringBuilder transpile(JxExpression expression, StringBuilder builder) {
		return transpile(expression, expression.children(), builder);
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
			case Node.TerminalNode terminalNode -> {
				builder.append(terminalNode.toString());
				// attach whitespaces after the last char
				int i = terminalNode.getEndOffset();
				while (terminalNode.getTokenSource().length() > i &&
						Character.isWhitespace(terminalNode.getTokenSource().charAt(i))) {
					builder.append(terminalNode.getTokenSource().charAt(i++));
				}
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
}
