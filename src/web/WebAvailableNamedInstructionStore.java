package web;

import java.util.Arrays;
import java.util.Collection;

import aya.AyaPrefs;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.UnimplementedError;
import aya.ext.sys.IsFileSystemInstruction;
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
			new IsFileSystemInstruction(),

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
			
			// Absolute Path
			new NamedOperator("sys.abspath", "convert path string to absolute path. Normalize the path if '.' or '..' specifiers exist") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					String path_str = blockEvaluator.pop().str();
					StringPath path = new StringPath(path_str);
					
					if (!path.isAbsolute()) {
						path = new StringPath(AyaPrefs.getWorkingDir()).join(path);
					}
					
					path = path.normalize();
					
					blockEvaluator.push(List.fromString(path.toString()));
				}
			},
			
			// Join Path
			new NamedOperator("sys.joinpath", "a::str b::str join two paths") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String b = blockEvaluator.pop().str();
					final String a = blockEvaluator.pop().str();
					
					final StringPath path = new StringPath(a);
					
					blockEvaluator.push(List.fromString(path.join(new StringPath(b)).toString()));
				}
			},
			
			// Get parent name from path
			new NamedOperator("sys.parent", "get the parent dir from a path") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String path = blockEvaluator.pop().str();
					blockEvaluator.push(List.fromString(new StringPath(path).getParent().toString()));
				}
			},
			
			// Get file name from path
			new NamedOperator("sys.get_filename", "get the filename from a path") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String arg = blockEvaluator.pop().str();
					blockEvaluator.push(List.fromString(new StringPath(arg).getName()));
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
