package aya.io.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;

public abstract class AbstractFilesystemIO {
	public abstract byte[] readAllBytes(File file) throws IOException;

	public abstract void write(File file, byte[] bytes, StandardOpenOption create,
			StandardOpenOption truncateExisting) throws IOException;

	public abstract boolean exists(File file);
	public abstract boolean isFile(File file);
}
