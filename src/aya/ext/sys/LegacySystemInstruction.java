package aya.ext.sys;

import java.io.File;
import java.util.ArrayList;

import aya.AyaPrefs;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.util.FileUtils;

public class LegacySystemInstruction extends NamedInstruction {
	
	public LegacySystemInstruction() {
		super("sys.MZ");
		_doc = ("system functions\n"
				+ "  S1:  change prompt text\n"
				+ "  A2:  get working dir\n"
				+ "  S3:  set working dir\n"
				+ "  \"\"3: reset working dir\n"
				+ "  S4:  list files in working dir + S\n"
				+ "  S5:  create dir in working dir + S\n"
				+ "  S6:  delete file or dir\n"
				+ "  S7:  get home dir + S\n"
				+ "  S8:  test if file exists at S\n"
				+ "  S9:  System.getProperty(S)\n"
				+ "  S10: Resolve home in path"
				);
	}

	@Override
	public void execute(Block block) {
		Obj cmd = block.pop();
		Obj arg = block.pop();
		
		if(cmd.isa(Obj.NUMBER)) {
			doCommand(((Number)cmd).toInt(), arg, block);
		} else {	
			throw new TypeError(this, "expected AN", cmd, arg);
		}
	}
	
	private void doCommand(int cmdID, Obj arg, Block b) {
		switch(cmdID) {
		
		//Change the prompt
		case 1:
			if(arg.isa(Obj.STR)) {
				AyaPrefs.setPrompt(arg.str());
			} else {
				throw new ValueError("arg 1 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
		
		//Return working directory
		case 2:
			b.push(List.fromString(AyaPrefs.getWorkingDir()));
			break;
			
		//Set working directory
		case 3:
			if (arg.isa(Obj.STR)) {
				String dir = arg.str();
				if(dir.equals("")) {
					AyaPrefs.resetWorkingDir();
				} else {
					if (!AyaPrefs.setWorkingDir(arg.str())) {
						throw new ValueError("arg 3 MZ: arg is not a valid path."
								+ " Did you include a '/' or '\' at the end? Received:\n" + arg.repr());
					}
				}
			}else {
				throw new ValueError("arg 3 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
		
		//List files in working directory
		case 4:
			if (arg.isa(Obj.STR)) {
				String fstr = arg.str();
				try {
					ArrayList<String> dirs = AyaPrefs.listFilesAndDirsForFolder(new File(fstr));
					ArrayList<Obj> obj_dirs = new ArrayList<Obj>(dirs.size());
					for (String s : dirs) {
						obj_dirs.add(List.fromString(s));
					}
					b.push(new List(obj_dirs));
				} catch (NullPointerException e) {
					throw new ValueError("arg 4 MZ: arg is not a valid location. Received:\n" + fstr);
				}
			} else {
				throw new ValueError("arg 4 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
			
		//Create dir
		case 5:
			if(arg.isa(Obj.STR)) {
				String fstr = arg.str();
				if(!AyaPrefs.mkDir(fstr)) {
					throw new ValueError("arg 5 MZ: arg must be a valid name. Received:\n" + fstr);
				}
			} else {
				throw new ValueError("arg 5 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
		
		//Delete
		case 6:
			if(arg.isa(Obj.STR)) {
				String arg_str = arg.str();
				if(arg_str.equals("")) {
					throw new ValueError("arg 5 MZ: arg must be a valid name. Received:\n" + arg_str);
				}
				String fstr = AyaPrefs.getWorkingDir() + arg.str();
				if(!AyaPrefs.deleteFile(fstr)) {
					throw new ValueError("arg 5 MZ: arg must be a valid name. Received:\n" + fstr);
				}
			} else {
				throw new ValueError("arg 5 MZ: arg must be a string. Received:\n" + arg.repr());
			}
			break;
		
		case 7:
			b.push(List.fromString(FileUtils.pathAppend(AyaPrefs.getHomeDir(), arg.str())));
			break;
			
		case 8:
			b.push(FileUtils.isFile(arg.str()) ? Num.ONE : Num.ZERO);
			break;
			
		case 9:
			{
				String val = System.getProperty(arg.str());
				if (val == null) {
					b.push(List.fromString(""));
				} else {
					b.push(List.fromString(val));
				}
			}
			break;
			
		case 10:
			b.push(List.fromString(FileUtils.resolveHome(arg.str())));
			break;
		
		default:
			throw new ValueError("arg " + cmdID + " MZ: is not a valid command ID");

		}
	}

}
