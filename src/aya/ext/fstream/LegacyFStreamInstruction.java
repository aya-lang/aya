package aya.ext.fstream;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.List;
import aya.obj.number.Num;

public class LegacyFStreamInstruction extends NamedOperator {
	
	public LegacyFStreamInstruction() {
		super("fstream.O");
		_doc = "Stream operations";
		/*
		arg("NC", "stream operations: l:readline, b:readchar, a:readall, c:close, f:flush, i:info");
		arg("SC", "open/close stream: w:write, r:read");
		arg("SN", "write to stream");
		*/
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		
		if (a.isa(Obj.CHAR) && b.isa(Obj.NUMBER)) {
			char c = ((Char)a).charValue();
			int i  = ((Num)b).toInt();
			
			switch (c) {
			case 'l':
				// Push 0 if invalid
				String line = FStreamManager.readline(i);
				if (line == null) {
					blockEvaluator.push(Num.ZERO);
				} else {
					blockEvaluator.push(List.fromString(line));
				}
				break;
			case 'b':
				// Since 0 is a valid byte, push -1 if invalid
				blockEvaluator.push(Num.fromInt(FStreamManager.read(i)));
				break;
			case 'a':
				// Pushes 0 if invalid
				String all = FStreamManager.readAll(i);
				if (all == null) {
					blockEvaluator.push(Num.ZERO);
				} else {
					blockEvaluator.push(List.fromString(all));
				}
				break;
			case 'c':
				// Close the file
				blockEvaluator.push(FStreamManager.close(i) ? Num.ONE : Num.ZERO);
				break;
			case 'f':
				// Flush
				blockEvaluator.push(FStreamManager.flush(i) ? Num.ONE : Num.ZERO);
				break;
			case 'i':
				// Info 0:does not exist, 1:input, 2:output
				blockEvaluator.push(Num.fromInt(FStreamManager.info(i)));
				break;
			default:
				throw new ValueError("Invalid char for operator 'O': " + c);
			}
			
		} else if (a.isa(Obj.NUMBER)) {
			int i = ((Num)a).toInt();
			blockEvaluator.push(FStreamManager.print(i, b.str()) ? Num.ONE : Num.ZERO);
		} else if (a.isa(Obj.CHAR)) {
			char c = ((Char)a).charValue();
			String filename = b.str();
			blockEvaluator.push(Num.fromInt(FStreamManager.open(filename, c+"")));
		} else {
			throw new TypeError(this, "Unexpected values ", a, b);
		}
	}

}