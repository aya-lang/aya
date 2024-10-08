package aya.ext.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import aya.AyaPrefs;
import aya.StaticData;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.util.Casting;

public class SysExecInstruction extends NamedOperator {
	
	public SysExecInstruction() {
		super("sys.exec");
		_doc = ("execute system commands");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
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
		    	StaticData.IO.out().println(line);
		    }
		 
		    reader.close();
		    StaticData.IO.out().println(command + ": finished!");
		 
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

}
