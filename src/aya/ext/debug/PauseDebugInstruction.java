package aya.ext.debug;

import aya.Aya;
import aya.ReprStream;
import aya.exceptions.parser.ParserException;
import aya.instruction.named.NamedOperator;
import aya.obj.block.BlockEvaluator;
import aya.parser.Parser;
import aya.parser.SourceString;

public class PauseDebugInstruction extends NamedOperator {
	
	public PauseDebugInstruction() {
		super("debug.pause");
		_doc = "Pause current execution and open a REPL";
	}
	
	private static void print(Object o) {
		Aya.getInstance().getOut().println(o);
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		print("Execution paused, enter '.' to continue");
		print("Stack: " + blockEvaluator.getOutputStateDebug());
		String instructions_state = blockEvaluator.getInstructions().repr(new ReprStream()).toString();
		if (instructions_state.length() > 100) {
			instructions_state = instructions_state.substring(0, 100) + "...";
		}
		print("Next instructions: " + instructions_state);
		while (true) {
			Aya.getInstance().getOut().print("aya (debug)> ");
			String input = Aya.getInstance().nextLine();
			if (input.equals(".")) {
				print(""); // newline
				break;
			}
			
			BlockEvaluator b = null;
			try {
				b = Parser.compile(new SourceString(input, "<debug>"), Aya.getInstance());
			} catch (ParserException e) {
				print("Error parsing expression '" + input + "':\n" + e.getMessage());
			}
			
			if (b != null) {
				b.eval();
				print(b.getPrintOutputState());
			}
		}
	}

}
