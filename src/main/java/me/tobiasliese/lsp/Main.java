package me.tobiasliese.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

public class Main {

	static int port = 5001;

	public static void main(String[] args) {
		LspServer server = new LspServer();

		Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(
			server,
			System.in,
			System.out
		);
		launcher.startListening();
	}
}
