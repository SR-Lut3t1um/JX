package me.tobiasliese.jx_tools;


import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.FieldSources;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.engine.support.descriptor.FileSource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class JxTranspilerTest {


	@ParameterizedTest
	@ValueSource(strings = {"example.jx"})
	public void transpile(String path) throws IOException {
		path = this.getClass().getClassLoader().getResource(path).getPath();
		JxTranspiler jxTranspiler = new JxTranspiler();
		System.out.println(jxTranspiler.transpile(new String(Files.readAllBytes(Path.of(path)))));
	}


}