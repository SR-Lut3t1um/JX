package me.tobiasliese.lsp;

import com.google.common.jimfs.File;
import com.google.common.jimfs.Jimfs;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.FileSystem;

import static org.mockito.Mockito.mock;

public class IntegrationTest {
    private static FileSystem fs;
    @BeforeAll
    static void setup() {
        fs = Jimfs.newFileSystem();

    }

    @Test
    void clientConnection() {
        var client = mock(LanguageClient.class);
        LanguageServer server = new LspServer(fs);
    }
}
