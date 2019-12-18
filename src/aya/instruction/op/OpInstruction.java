package aya.instruction.op;

import java.util.LinkedList;

import aya.instruction.Instruction;
import aya.instruction.op.overload.OpOverload;

/**
 * The Operation Class
 * Every operator has some basic information (name, desc, argtypes)
 * and an execute method. The execute method is called by the interpreter
 * at run time and can manipulate a block
 * 
 * @author npaul
 *
 */
public abstract class OpInstruction extends Instruction {

	public String name;
	public OpOverload overload;
	public OpDoc _doc;
	
	public String getDocTypeStr() {
		if (_doc == null) {
			return "No docs provided for " + name;
		} else {
			return _doc.typeString();
		}
		
	}
	

	public String getName() {
		return name;
	}
	
	public void init(String name) {
		this.name = name;
		if (name.length() == 1) {
			this._doc = new OpDoc(' ', name);
		} else if (name.length() == 2) {
			this._doc = new OpDoc(name.charAt(0), name);
		} else {
			throw new IllegalArgumentException("OpInstruction name must be exactly 1 or 2 chars");
		}
	}
	
	public void arg(String type, String desc) {
		if (_doc == null) throw new RuntimeException("OpInstruction.init not called!");
		
		_doc.desc(type, desc);
	}
	
	public boolean hasDocs() {
		return _doc != null;
	}
	
	public OpDoc getDoc() {
		if (_doc == null) throw new RuntimeException("Doc does not exist");
		return _doc;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	protected String repr(LinkedList<Long> visited) {
		return name;
	}
}
