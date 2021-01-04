package aya.ext.debug;

import aya.Aya;
import aya.ReprStream;
import aya.instruction.named.NamedInstruction;
import aya.obj.block.Block;
import aya.parser.Parser;

public class PauseDebugInstruction extends NamedInstruction {
	
	public PauseDebugInstruction() {
		super("debug.pause");
		_doc = "Pause current execution and open a REPL";
	}
	
	private static void print(Object o) {
		Aya.getInstance().println(o);
	}

	@Override
	public void execute(Block block) {
		print("Execution paused, enter '.' to continue");
		print("Stack: " + block.getOutputStateDebug());
		String instructions_state = block.getInstructions().repr(new ReprStream()).toString();
		if (instructions_state.length() > 100) {
			instructions_state = instructions_state.substring(0, 100) + "...";
		}
		print("Next instructions: " + instructions_state);
		while (true) {
			Aya.getInstance().print("aya (debug)> ");
			String input = Aya.getInstance().nextLine();
			if (input.equals(".")) {
				print(""); // newline
				break;
			}
			Block b = Parser.compile(input, Aya.getInstance());
			b.eval();
			print(b.getPrintOutputState());
		}
	}

}
