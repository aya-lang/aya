package aya.ext.sys;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import aya.AyaPrefs;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.ValueError;
import aya.exceptions.runtime.IOError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class SystemInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(

			// Exec
			new SysExecInstruction(),
	
			// Readdir
			new NamedOperator("sys.readdir", "list files in working dir") {
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
			},
			
			// Is dir?
			new NamedOperator("sys.isdir", "true if the path exists and is a directory") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String path_str = blockEvaluator.pop().str();
					File file = FileUtils.resolvePath(path_str).toFile();
					blockEvaluator.push(Num.fromBool(file.exists() && file.isDirectory()));
				}
			},


			// Get working dir
			new NamedOperator("sys.wd", "get absolute path of working dir") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					blockEvaluator.push(List.fromString(AyaPrefs.getWorkingDir()));
				}
			},

			// Get aya dir
			new NamedOperator("sys.ad", "get absolute path of aya dir") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					blockEvaluator.push(List.fromString(AyaPrefs.getAyaDir()));
				}
			},

			// Set aya dir
			new NamedOperator("sys.set_ad", "set absolute path of aya dir") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					AyaPrefs.setAyaDir(blockEvaluator.pop().str());
				}
			},

			// Set working dir
			new NamedOperator("sys.cd", "set the working dir (empy string resets to default)") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final Obj arg = blockEvaluator.pop();

					if (arg.isa(Obj.STR)) {
						String dir = arg.str();
						if (dir.equals("")) {
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
			},

			// Make dir
			new NamedOperator("sys.mkdir", "create a directory") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final Obj arg = blockEvaluator.pop();

					if (arg.isa(Obj.STR)) {
						String fstr = arg.str();
						if (!AyaPrefs.mkDir(fstr)) {
							throw new ValueError(":{sys.mkdir} : arg must be a valid name. Received:\n" + fstr);
						}
					} else {
						throw new ValueError(":{sys.mkdir} : arg must be a string. Received:\n" + arg.repr());
					}
				}
			},

			// System.getProperty
			new NamedOperator("sys.getprop", "call System.getProperty with the given key") {
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
			},

			// Delete file or directory
			new NamedOperator("sys.rm", "remove a file or directory") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final Obj arg = blockEvaluator.pop();

					if (arg.isa(Obj.STR)) {
						String arg_str = arg.str();
						if (arg_str.equals("")) {
							throw new ValueError(":{sys.rm} : arg must be a valid name. Received:\n" + arg_str);
						}
						File delFile = FileUtils.resolveFile(arg.str());
						if (!AyaPrefs.deleteFile(delFile)) {
							throw new ValueError(":{sys.rm} : arg must be a valid name. Received:\n" + delFile.getAbsolutePath());
						}
					} else {
						throw new ValueError(":{sys.rm} : arg must be a string. Received:\n" + arg.repr());
					}
				}
			},
		
			// Test if file exists
			new FileExistsSystemInstruction(),

			// Resolve home (replace ~/ with /path/to/home)
			new NamedOperator("sys.resolvehome", "replace ~/.. with /path/to/home/..") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final Obj arg = blockEvaluator.pop();
					blockEvaluator.push(List.fromString(FileUtils.resolveHome(arg.str())));
				}
			},
			
			
			// Absolute Path
			new NamedOperator("sys.abspath", "convert path string to absolute path. Normalize the path if '.' or '..' specifiers exist") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String path_str = blockEvaluator.pop().str();
					final Path p = FileUtils.resolvePath(path_str).normalize();
					blockEvaluator.push(List.fromString( p.toFile().getAbsolutePath()) );
				}
			},
			
			// Get file name from path
			new NamedOperator("sys.get_filename", "get the filename from a path") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String arg = blockEvaluator.pop().str();
					blockEvaluator.push(List.fromString(new File(arg).getName()));
				}
			},
			
			// Get parent name from path
			new NamedOperator("sys.parent", "get the parent dir from a path") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String arg = blockEvaluator.pop().str();
					String parent = new File(arg).getParent();
					if (parent == null) {
						parent = "";
					}
					blockEvaluator.push(List.fromString(parent));
				}
			},

			// Change the prompt text
			new NamedOperator("sys.alterprompt", "change the prompt text") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final Obj arg = blockEvaluator.pop();
					AyaPrefs.setPrompt(arg.str());
				}
			},

			new NamedOperator("sys.args", "CLI args") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					List args = new List();
					for (String a : AyaPrefs.getArgs()) {
						args.mutAdd(List.fromString(a));
					}
					blockEvaluator.push(args);
				}
			},

			new NamedOperator("sys.unzip", "zip_path::str dest_path::str unzip a file into a directory") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String dest_path = blockEvaluator.pop().str();
					final String zip_path  = blockEvaluator.pop().str();

					try {
						FileUtils.unzip(FileUtils.resolveFile(zip_path), FileUtils.resolvePath(dest_path));
					} catch (IOException e) {
						throw new IOError("sys.unzip", zip_path + " -> " + dest_path, e);
					}
				}
			},

			new NamedOperator("sys.mvdir", "src::str dst::str rename/move a directory") {
				@Override
				public void execute(BlockEvaluator blockEvaluator) {
					final String dest_path = blockEvaluator.pop().str();
					final String src_path  = blockEvaluator.pop().str();

					try {
						FileUtils.moveDir(FileUtils.resolvePath(src_path), FileUtils.resolvePath(dest_path));
					} catch (IOException e) {
						throw new IOError("sys.mvdir", src_path + " -> " + dest_path, e);
					}
				}
			}
		);
	}
}
