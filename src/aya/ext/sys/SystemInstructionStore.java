package aya.ext.sys;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import aya.AyaPrefs;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.util.FileUtils;

public class SystemInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// Exec
		addInstruction(new SysExecInstruction());
		
		// Readdir
		addInstruction(new NamedOperator("sys.readdir", "list files in working dir") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();
				
				if (arg.isa(Obj.STR)) {
					String fstr = arg.str();
					Path path = FileUtils.resolvePath(fstr);
					ArrayList<String> dirs = AyaPrefs.listFilesAndDirsForFolder(path);
					ArrayList<Obj> obj_dirs = new ArrayList<Obj>(dirs.size());
					for (String s : dirs) {
						obj_dirs.add(List.fromString(s));
					}
					blockEvaluator.push(new List(obj_dirs));
				} else {
					throw new ValueError(":(sys.readdir) : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		// Get working dir
		addInstruction(new NamedOperator("sys.wd", "get absolute path of working dir") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				blockEvaluator.push(List.fromString(AyaPrefs.getWorkingDir()));
			}
		});

		// Get aya dir
		addInstruction(new NamedOperator("sys.ad", "get absolute path of aya dir") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				blockEvaluator.push(List.fromString(AyaPrefs.getAyaDir()));
			}
		});
		
		// Get aya dir
		addInstruction(new NamedOperator("sys.set_ad", "set absolute path of aya dir") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				AyaPrefs.setAyaDir(blockEvaluator.pop().str());
			}
		});
		
		// Set working dir
		addInstruction(new NamedOperator("sys.cd", "set the working dir (empy string resets to default)") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();

				if (arg.isa(Obj.STR)) {
					String dir = arg.str();
					if(dir.equals("")) {
						AyaPrefs.resetWorkingDir();
					} else {
						if (!AyaPrefs.setWorkingDir(arg.str())) {
							throw new ValueError(":(sys.cd) : arg is not a valid path."
									+ " Did you include a '/' or '\' at the end? Received:\n" + arg.repr());
						}
					}
				} else {
					throw new ValueError(":(sys.cd) : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		// Make dir
		addInstruction(new NamedOperator("sys.mkdir", "create a directory") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();

				if(arg.isa(Obj.STR)) {
					String fstr = arg.str();
					if(!AyaPrefs.mkDir(fstr)) {
						throw new ValueError(":(sys.mkdir) : arg must be a valid name. Received:\n" + fstr);
					}
				} else {
					throw new ValueError(":(sys.mkdir) : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		
		// System.getProperty
		addInstruction(new NamedOperator("sys.getprop", "call System.getProperty with the given key") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();
				String val = System.getProperty(arg.str());
				if (val == null) {
					blockEvaluator.push(List.fromString(""));
				} else {
					blockEvaluator.push(List.fromString(val));
				}
			}
		});
		
		// Delete file or directory
		addInstruction(new NamedOperator("sys.rm", "remove a file or directory") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();

				if(arg.isa(Obj.STR)) {
					String arg_str = arg.str();
					if(arg_str.equals("")) {
						throw new ValueError(":(sys.rm) : arg must be a valid name. Received:\n" + arg_str);
					}
					File delFile = FileUtils.resolveFile(arg.str());
					if(!AyaPrefs.deleteFile(delFile)) {
						throw new ValueError(":(sys.rm) : arg must be a valid name. Received:\n" + delFile.getAbsolutePath());
					}
				} else {
					throw new ValueError(":(sys.rm) : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		addInstruction(new FileExistsSystemInstruction());
		
		// Resolve home (replace ~/ with /path/to/home)
		addInstruction(new NamedOperator("sys.resolvehome", "replace ~/.. with /path/to/home/..") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();
				blockEvaluator.push(List.fromString(FileUtils.resolveHome(arg.str())));
			}
		});
		
		// Change the prompt text
		addInstruction(new NamedOperator("sys.alterprompt", "change the prompt text") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				final Obj arg = blockEvaluator.pop();
				AyaPrefs.setPrompt(arg.str());
			}
		});
		
		addInstruction(new NamedOperator("sys.args", "CLI args") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				List args = new List();
				for (String a : AyaPrefs.getArgs()) {
					args.mutAdd(List.fromString(a));
				}
				blockEvaluator.push(args);
			}
		});
	}
}
