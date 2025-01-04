package aya.ext.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Collection;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.util.FileUtils;

public class DownloadInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
			new NamedOperator("download.to_file", "address::str file::str: Download a file from the address to the file location") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String file_path = blockEvaluator.pop().str();
					final String address   = blockEvaluator.pop().str();
					
					try {
						URL dl_url = new URL(address);
						try (ReadableByteChannel readableByteChannel = Channels.newChannel(dl_url.openStream())) {
							try (FileOutputStream fileOutputStream = new FileOutputStream(FileUtils.resolveFile(file_path))) {
								FileChannel fileChannel = fileOutputStream.getChannel();
								fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
							}
						} catch (IOException e) {
							throw new IOError("download.to_file", address + " -> " + file_path, e);
						}
					} catch (MalformedURLException e) {
						throw new ValueError("Malformed URL: " + address);
					}
				}
			}
		);
	}
}
