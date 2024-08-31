package aya.ext.sys;

import java.io.File;
import java.util.ArrayList;

import aya.AyaPrefs;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class SystemInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// Exec
		addInstruction(new SysExecInstruction());
		
		// Readdir
		addInstruction(new NamedInstruction("sys.readdir", "list files in working dir") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();
				
				if (arg.isa(Obj.STR)) {
					String fstr = arg.str();
					try {
						ArrayList<String> dirs = AyaPrefs.listFilesAndDirsForFolder(new File(fstr));
						ArrayList<Obj> obj_dirs = new ArrayList<Obj>(dirs.size());
						for (String s : dirs) {
							obj_dirs.add(List.fromString(s));
						}
						block.push(new List(obj_dirs));
					} catch (NullPointerException e) {
						throw new ValueError(":{sys.readdir} : arg is not a valid location. Received:\n'" + fstr + "'");
					}
				} else {
					throw new ValueError(":{sys.readdir} : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		// Get working dir
		addInstruction(new NamedInstruction("sys.wd", "get absolute path of working dir") {
			@Override
			public void execute(Block block) {
				block.push(List.fromString(AyaPrefs.getWorkingDir()));
			}
		});

		// Get aya dir
		addInstruction(new NamedInstruction("sys.ad", "get absolute path of aya dir") {
			@Override
			public void execute(Block block) {
				block.push(List.fromString(AyaPrefs.getAyaDir()));
			}
		});
		
		// Get aya dir
		addInstruction(new NamedInstruction("sys.set_ad", "set absolute path of aya dir") {
			@Override
			public void execute(Block block) {
				AyaPrefs.setAyaDir(block.pop().str());
			}
		});
		
		// Set working dir
		addInstruction(new NamedInstruction("sys.cd", "set the working dir (empy string resets to default)") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();

				if (arg.isa(Obj.STR)) {
					String dir = arg.str();
					if(dir.equals("")) {
						AyaPrefs.resetWorkingDir();
					} else {
						if (!AyaPrefs.setWorkingDir(arg.str())) {
							throw new ValueError(":{sys.cd} : arg is not a valid path."
									+ " Did you include a '/' or '\' at the end? Received:\n" + arg.repr());
						}
					}
				} else {
					throw new ValueError(":{sys.cd} : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		// Make dir
		addInstruction(new NamedInstruction("sys.mkdir", "create a directory") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();

				if(arg.isa(Obj.STR)) {
					String fstr = arg.str();
					if(!AyaPrefs.mkDir(fstr)) {
						throw new ValueError(":{sys.mkdir} : arg must be a valid name. Received:\n" + fstr);
					}
				} else {
					throw new ValueError(":{sys.mkdir} : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		
		// System.getProperty
		addInstruction(new NamedInstruction("sys.getprop", "call System.getProperty with the given key") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();
				String val = System.getProperty(arg.str());
				if (val == null) {
					block.push(List.fromString(""));
				} else {
					block.push(List.fromString(val));
				}
			}
		});
		
		// Delete file or directory
		addInstruction(new NamedInstruction("sys.rm", "remove a file or directory") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();

				if(arg.isa(Obj.STR)) {
					String arg_str = arg.str();
					if(arg_str.equals("")) {
						throw new ValueError(":{sys.rm} : arg must be a valid name. Received:\n" + arg_str);
					}
					File delFile = FileUtils.resolveFile(arg.str());
					if(!AyaPrefs.deleteFile(delFile)) {
						throw new ValueError(":{sys.rm} : arg must be a valid name. Received:\n" + delFile.getAbsolutePath());
					}
				} else {
					throw new ValueError(":{sys.rm} : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		// Test if file exists
		addInstruction(new NamedInstruction("sys.file_exists", "test if the file exists") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();
				block.push(FileUtils.isFile(arg.str()) ? Num.ONE : Num.ZERO);
			}
		});
		
		// Resolve home (replace ~/ with /path/to/home)
		addInstruction(new NamedInstruction("sys.resolvehome", "replace ~/.. with /path/to/home/..") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();
				block.push(List.fromString(FileUtils.resolveHome(arg.str())));
			}
		});
		
		// Change the prompt text
		addInstruction(new NamedInstruction("sys.alterprompt", "change the prompt text") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();
				AyaPrefs.setPrompt(arg.str());
			}
		});
	}
}
