package me.tobiasliese;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.parsers.java.JavaParser;
import org.parsers.java.ast.PrimaryExpression;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

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
