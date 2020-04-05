package aya.ext.sys;

import java.io.File;
import java.util.ArrayList;

import aya.AyaPrefs;
import aya.exceptions.AyaRuntimeException;
import aya.instruction.named.NamedInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.obj.list.StrList;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class SystemInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		addInstruction(new LegacySystemInstruction());
		
		// Readdir
		addInstruction(new NamedInstruction("sys.readdir", "list files in working dir") {
			@Override
			public void execute(Block block) {
				final Obj arg = block.pop();
				
				if (arg.isa(Obj.STR)) {
					String fstr = arg.str();
					try {
						ArrayList<String> dirs = AyaPrefs.listFilesAndDirsForFolder(new File(fstr));
						ArrayList<Str> obj_dirs = new ArrayList<Str>(dirs.size());
						for (String s : dirs) {
							obj_dirs.add(new Str(s));
						}
						block.push(new StrList(obj_dirs));
					} catch (NullPointerException e) {
						throw new AyaRuntimeException(":{sys.readdir} : arg is not a valid location. Received:\n" + fstr);
					}
				} else {
					throw new AyaRuntimeException(":{sys.readdir} : arg must be a string. Received:\n" + arg.repr());
				}
			}
		});
		
		// Get working dir
		addInstruction(new NamedInstruction("sys.wd", "get absolute path of working dir") {
			@Override
			public void execute(Block block) {
				block.push(new Str(AyaPrefs.getWorkingDir()));
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
							throw new AyaRuntimeException("arg 3 MZ: arg is not a valid path."
									+ " Did you include a '/' or '\' at the end? Received:\n" + arg.repr());
						}
					}
				} else {
					throw new AyaRuntimeException("arg 3 MZ: arg must be a string. Received:\n" + arg.repr());
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
						throw new AyaRuntimeException(":{sys.mkdir} : arg must be a valid name. Received:\n" + fstr);
					}
				} else {
					throw new AyaRuntimeException(":{sys.mkdir} : arg must be a string. Received:\n" + arg.repr());
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
					block.push(Str.EMPTY);
				} else {
					block.push(new Str(val));
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
						throw new AyaRuntimeException(":{sys.rm} : arg must be a valid name. Received:\n" + arg_str);
					}
					String fstr = AyaPrefs.getWorkingDir() + arg.str();
					if(!AyaPrefs.deleteFile(fstr)) {
						throw new AyaRuntimeException(":{sys.rm} : arg must be a valid name. Received:\n" + fstr);
					}
				} else {
					throw new AyaRuntimeException(":{sys.rm} : arg must be a string. Received:\n" + arg.repr());
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
				block.push(new Str(FileUtils.resolveHome(arg.str())));
			}
		});
	}
}
