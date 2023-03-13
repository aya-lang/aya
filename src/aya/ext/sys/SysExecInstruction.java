package aya.ext.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import aya.Aya;
import aya.AyaPrefs;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.util.Casting;

public class SysExecInstruction extends NamedInstruction {
	
	public SysExecInstruction() {
		super("sys.exec");
		_doc = ("execute system commands");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		List commands;
		
		if (a.isa(Obj.LIST)) {
			commands = Casting.asList(a);
		} else {
			throw new TypeError(this, "list", a);
		}
		
		ArrayList<String> command = new ArrayList<String>();
		for (int i = 0; i < commands.length(); i++) {
			command.add(commands.getExact(i).str());
		}

		try {
		    ProcessBuilder builder = new ProcessBuilder(command);
		    builder.redirectErrorStream(true);
		    builder.directory(new File(AyaPrefs.getWorkingDir()));
		    Process proc = builder.start();

		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(proc.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	Aya.getInstance().getOut().println(line);
		    }
		 
		    reader.close();
		    Aya.getInstance().getOut().println(command + ": finished!");
		 
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

}
