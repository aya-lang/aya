package aya.exceptions.runtime;

import java.io.PrintStream;
import java.util.ArrayList;

import aya.ReprStream;
import aya.exceptions.ex.AyaExceptionInterface;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public abstract class AyaRuntimeException extends RuntimeException implements AyaExceptionInterface {
	String msg;
	ArrayList<String> context;
	
	public AyaRuntimeException(String msg) {
		super(msg);
		this.msg = msg;
		context = null;
	}
	
	public AyaRuntimeException(Exception e) {
		super(e.getMessage());
		this.msg = e.getMessage();
	}

	public String getSimpleMessage() {
		return msg;
	}
	
	public abstract Symbol typeSymbol();
	
	public Obj getObj() {
		Dict d = new Dict();
		d.set(SymbolConstants.TYPE, typeSymbol());
		d.set(SymbolConstants.MSG, List.fromString(msg));
		return d;
	}

	public void addContext(String str) {
		if (context == null) context = new ArrayList<String>();
		context.add(str);
	}
	
	public void addContext(Instruction instr) {
		if (instr.getSource() != null) {
			addContext(instr.getSource().getContextStr());
		}
	}
	
	public void addContext(Instruction instr, Block block) {
		if (instr.getSource() != null) {
			addContext(instr);
		} else {
			// Legacy version
			try {
				String ctx = "> ";
				if (instr != null) {
					ctx += instr.repr(new ReprStream()) + " ";
				}
				ctx += ".. " + block.repr(new ReprStream(), false).toStringOneline() + "}";
				addContext(ctx);
			} catch (Exception e) {
				addContext("<exception occurred: " + e.getMessage() + ">");
			}
		}
	}

	public void print(PrintStream stream) {
		stream.println(getMessage());
		if (context != null && context.size() > 0) {
			stream.println("");
			stream.println(context.get(0));
			//for (String s : context) {
			//	stream.println(s);
			//}
		}
	}
	
}
