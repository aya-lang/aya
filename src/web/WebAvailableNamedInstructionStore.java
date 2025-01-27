package web;

import java.util.Arrays;
import java.util.Collection;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.UnimplementedError;
import aya.ext.sys.FileExistsSystemInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.list.List;
import aya.obj.list.Str;

public class WebAvailableNamedInstructionStore implements NamedInstructionStore {
	/**
	 * This class provides some overrides for aya instructions so they work in the web implementation
	 * 
	 * Only a small subset of instructions are supported
	 */

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
			new FileExistsSystemInstruction(),

			new NamedOperator("sys.ad", "get absolute path of aya dir") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					blockEvaluator.push(List.fromStr(Str.EMPTY));
				}
			},

			new NamedOperator("sys.wd", "get absolute path of working dir") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					blockEvaluator.push(List.fromStr(Str.EMPTY));
				}
			},

			new NamedOperator("debug.pause", "pause execution and open a repl") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					// Unimplemented
					throw new UnimplementedError();
				}
			},
			
			new NamedOperator("library.load", "load a jar file") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					// Unimplemented
					throw new UnimplementedError();
				}
			}
		);
	}

}
