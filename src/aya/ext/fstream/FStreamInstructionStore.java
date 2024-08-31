package aya.ext.fstream;

import java.io.IOException;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.util.FileUtils;

public class FStreamInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new LegacyFStreamInstruction());
		
		addInstruction(new NamedOperator("fstream.readallbytes") {
			
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				String path = blockEvaluator.pop().str();
				try {
					byte[] bytes = FileUtils.readAllBytes(path);
					double[] doubles = new double[bytes.length];
					for (int i = 0; i < bytes.length; i++) {
						doubles[i] = bytes[i];
					}
					blockEvaluator.push(new List(new DoubleList(doubles)));
				} catch (IOException e) {
					throw new IOError("{fstream.readallbytes}", path, e);
				}
			}
		});
	}
}
