package aya.io.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;

import aya.exceptions.runtime.IOError;

public class UnimplementedFilesystemIO extends AbstractFilesystemIO {

	@Override
	public byte[] readAllBytes(File file) throws IOException {
		throw new IOError("", "UnimplementedFilesystemReader.readAllBytes", "filesystem unavailable");
	}

	@Override
	public void write(File file, byte[] bytes, StandardOpenOption create, StandardOpenOption truncateExisting)
			throws IOException {
		throw new IOError("", "UnimplementedFilesystemReader.write", "filesystem unavailable");
	}

	@Override
	public boolean exists(File file) {
		throw new IOError("", "UnimplementedFilesystemReader.exists", "filesystem unavailable");
	}

	@Override
	public boolean isFile(File file) {
		throw new IOError("", "UnimplementedFilesystemReader.isFile", "filesystem unavailable");
	}

}
