package aya.ext.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import aya.StaticData;
import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.list.List;
import aya.util.FileUtils;

public class LibraryInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
			new NamedOperator("library.load", "path::str: load an external library, return list of loaded operators") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					String lib_path = blockEvaluator.pop().str();
					ArrayList<NamedInstructionStore> loaded = StaticData.getInstance().loadLibrary(FileUtils.resolveFile(lib_path));
					List loaded_names = new List();
					for (NamedInstructionStore lib : loaded) {
						for (NamedOperator op : lib.getNamedInstructions()) {
							loaded_names.mutAdd(List.fromString(op.opName()));
						}
					}
					blockEvaluator.push(loaded_names);
				}
			}
		);
	}
}
