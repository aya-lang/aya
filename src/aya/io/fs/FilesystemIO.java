package aya.io.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FilesystemIO extends AbstractFilesystemIO {

	@Override
	public byte[] readAllBytes(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}

	@Override
	public void write(File file, byte[] bytes, StandardOpenOption create, StandardOpenOption truncateExisting) throws IOException {
		Files.write(file.toPath(), bytes, create, truncateExisting);
	}

	@Override
	public boolean isFile(File file) {
		return file.isFile();
	}

}
