package me.tobiasliese.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class JxTextDocumentService implements TextDocumentService {

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        params.getTextDocument().getUri();
        List<ColorInformation> informations = new ArrayList<>();

        // informations.add();
        return TextDocumentService.super.documentColor(params);
    }

    @Override
    public CompletableFuture<List<ColorPresentation>> colorPresentation(ColorPresentationParams params) {
        List<ColorPresentation> colorPresentations = new ArrayList<>();
        // colorPresentations.add(new ColorPresentation().set);
        return CompletableFuture.completedFuture(colorPresentations);
    }

    @Override
	public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
		/*text.put(didOpenTextDocumentParams.getTextDocument().getUri(), didOpenTextDocumentParams
				.getTextDocument()
				.getText()
				.lines()
				.collect(Collectors.toCollection(ArrayList::new))
		);*/
	}

	@Override
	public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
		List<TextDocumentContentChangeEvent> changes = didChangeTextDocumentParams.getContentChanges();
		for (var change : changes) {
            change.getRange().getStart();
		}
	}



	@Override
	public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {

	}

	@Override
	public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {

	}

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        return TextDocumentService.super.hover(params);
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        return TextDocumentService.super.codeLens(params);
    }

    @Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
		return CompletableFuture.completedFuture(Either.forLeft(List.of()));
	}
}
