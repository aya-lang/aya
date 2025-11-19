package aya.ext.fstream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.util.FileUtils;

public class FStreamInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(

				// Legacy Stream Instructions
				// TODO: Separate into individual instructions
				new LegacyFStreamInstruction(),

				//
				// Utility Instructions
				//

				new NamedOperator("fileutils.readallbytes", "file::str -> bytes::[num]list read all bytes from a file") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						String path = blockEvaluator.pop().str();
						try {
							byte[] bytes = FileUtils.readAllBytes(FileUtils.resolveFile(path));
							double[] doubles = new double[bytes.length];
							for (int i = 0; i < bytes.length; i++) {
								doubles[i] = bytes[i];
							}
							blockEvaluator.push(new List(new DoubleList(doubles)));
						} catch (IOException e) {
							throw new IOError("{fileutils.readallbytes}", path, e);
						}
					}
				}
		);
	}
}
