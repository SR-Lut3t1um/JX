package me.tobiasliese.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class LspServer implements LanguageServer {

    private final FileSystem fs;

    public LspServer() {
        this(FileSystems.getDefault());
    }

    public LspServer(FileSystem fs) {
        this.fs = fs;
    }

    @Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {

        for (var folder: initializeParams.getWorkspaceFolders()) {
            String uri = folder.getUri();
        }
        var result = new InitializeResult();

        var serverInfo = new ServerInfo();
        serverInfo.setName("JX");
        serverInfo.setVersion("0.0.1");

        var serverCapabilities = new ServerCapabilities();
        var workspaceFolderOptions = new WorkspaceFoldersOptions();

        var fileOperationsServerCapabilities = new FileOperationsServerCapabilities();

        var fileOperationOptions = new FileOperationOptions();
        List<FileOperationFilter> filters = new ArrayList<>();

        var javaFileFilter = new FileOperationFilter();
        var javaFilePattern = new FileOperationPattern();
        javaFilePattern.setGlob("**/*.java");
        javaFileFilter.setPattern(javaFilePattern);

        var jxFileFilter = new FileOperationFilter();
        var jxFilePattern = new FileOperationPattern();
        jxFilePattern.setGlob("**/*.jx");
        jxFileFilter.setPattern(jxFilePattern);

        filters.add(javaFileFilter);
        filters.add(jxFileFilter);
        fileOperationOptions.setFilters(filters);

        fileOperationsServerCapabilities.setDidCreate(fileOperationOptions);
        fileOperationsServerCapabilities.setDidDelete(fileOperationOptions);
        fileOperationsServerCapabilities.setDidRename(fileOperationOptions);
        fileOperationsServerCapabilities.setWillCreate(fileOperationOptions);
        fileOperationsServerCapabilities.setWillDelete(fileOperationOptions);
        fileOperationsServerCapabilities.setWillRename(fileOperationOptions);

        workspaceFolderOptions.setChangeNotifications(true);
        serverCapabilities.getWorkspace().setWorkspaceFolders(workspaceFolderOptions);
        serverCapabilities.getWorkspace().setFileOperations(fileOperationsServerCapabilities);

        var textDocumentSyncOptions = new TextDocumentSyncOptions();
        textDocumentSyncOptions.setChange(TextDocumentSyncKind.Incremental);

        serverCapabilities.setTextDocumentSync(textDocumentSyncOptions);

        result.setServerInfo(serverInfo);
        result.setCapabilities(serverCapabilities);
		return CompletableFuture.completedFuture(result);
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
