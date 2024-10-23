package me.tobiasliese;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.parsers.java.JavaParser;
import org.parsers.java.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class JxTextDocumentService implements TextDocumentService {
	private ArrayList<String> text;
	private JavaParser parser;

	@Override
	public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
		assert text == null;
		text = didOpenTextDocumentParams
				.getTextDocument()
				.getText()
				.lines()
				.collect(Collectors.toCollection(ArrayList::new)
		);
		parser = new JavaParser(String.join("\n", text));
	}

	@Override
	public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
		List<TextDocumentContentChangeEvent> changes = didChangeTextDocumentParams.getContentChanges();
		for (var change : changes) {
			Position start = change.getRange().getStart();
			Position end = change.getRange().getEnd();

			List<String> chng = change.getText().lines().collect(Collectors.toCollection(ArrayList::new));

			String prefix = text.get(start.getLine()).substring(0, start.getCharacter());
			String suffix = text.get(end.getLine()).substring(end.getCharacter());

			text.set(start.getLine(), prefix + chng.getFirst());
			for (int i = start.getLine() + 1; i <= end.getLine() - 1; i++) {
				text.set(i, chng.get(i - start.getLine()));
			}
			text.set(end.getLine(), chng.getLast() + suffix);
		}
	}

	@Override
	public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
		text = null;
	}

	@Override
	public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
		Node root = parser.Root();

	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {

		return CompletableFuture.completedFuture(Either.forLeft(List.of()));
	}
}
