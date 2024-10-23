package me.tobiasliese;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

public class LspServer implements LanguageServer {
	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
		return CompletableFuture.completedFuture(new InitializeResult());
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		return null;
	}

	@Override
	public void exit() {

	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return new JxTextDocumentService();
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		return new JxWorkspaceService();
	}
}
