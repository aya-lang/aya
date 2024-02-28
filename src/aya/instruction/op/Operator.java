package aya.instruction.op;

import java.util.ArrayList;

import aya.ReprStream;
import aya.exceptions.runtime.UnimplementedError;
import aya.instruction.op.overload.OpOverload;
import aya.instruction.op.overload.OpOverload1Arg;
import aya.instruction.op.overload.OpOverload2Arg;
import aya.instruction.op.overload.OpOverloadNoOp;
import aya.obj.Obj;
import aya.obj.block.BlockEvaluator;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.SymbolConstants;

/**
 * The Operator Class
 * Every operator has some basic information (name, desc, argtypes)
 * and an execute method. The execute method is called by the interpreter
 * at run time and can manipulate a blockEvaluator
 * 
 * @author npaul
 *
 */
public abstract class Operator {

	public String name;
	private OpOverload _overload;
	public OpDoc _doc;
	
	public abstract void execute(BlockEvaluator blockEvaluator);
	
	public String getDocTypeStr() {
		if (_doc == null) {
			return "No docs provided for " + name;
		} else {
			return _doc.typeString();
		}
		
	}

	public void setOverload(int num, String name) {
		if (num == -1) {
			this._overload = new OpOverloadNoOp(name);
		} else if (num == 1) {
			this._overload = new OpOverload1Arg(name);
		} else if (num == 2) {
			this._overload = new OpOverload2Arg(name);
		} else {
			throw new IllegalArgumentException("Overload must be -1, 1, or 2");
		}

		this._doc.setOverloadNames(_overload.getNames());
	}
	
	public OpOverload overload() {
		return this._overload;
	}
	

	public String getName() {
		return name;
	}
	
	public void vect() {
		_doc.vect();
	}
	
	public void init(String name) {
		this.name = name;
		if (name.length() == 1) {
			this._doc = new OpDoc(' ', name);
		} else if (name.length() == 2) {
			this._doc = new OpDoc(name.charAt(0), name);
		} else {
			throw new IllegalArgumentException("Operator name must be exactly 1 or 2 chars");
		}
	}
	
	public void arg(String type, String desc) {
		if (_doc == null) throw new RuntimeException("Operator.init not called!");
		
		_doc.desc(type, desc);
	}
	
	public boolean hasDocs() {
		return _doc != null;
	}
	
	public OpDoc getDoc() {
		if (_doc == null) throw new RuntimeException("Doc does not exist");
		return _doc;
	}
	
	
	public Dict getInfo() {
		Dict info = new Dict();

		// {op}:call
		StaticBlock call = BlockUtils.makeBlockWithSingleInstruction(new OperatorInstruction(null, this));
		info.set(SymbolConstants.CALL, call);
		
		// [::sym]:symbols;
		ArrayList<Obj> symbols = new ArrayList<Obj>();
		if (_overload != null) symbols.addAll(_overload.getSymbols());
		info.set(SymbolConstants.OVERLOAD, new List(symbols));
		
		// "":name;
		info.set(SymbolConstants.NAME, List.fromString(this.getName()));
		
		// ::sym:type;
		info.set(SymbolConstants.TYPE, _doc.typeSymbol());
		
		// {,}:doc;
		info.set(SymbolConstants.DOC, _doc.toDict());
		
		return info;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public ReprStream repr(ReprStream stream) {
		stream.print(name);
		return stream;
	}

	public String getSymName() {
		if (_overload != null) {
			return _overload.getSymName();
		} else {
			return null;
		}
	}

	public Obj exec2arg(Obj a, Obj b) {
		throw new UnimplementedError();
	}

	public Obj exec1arg(Obj a) {
		throw new UnimplementedError();
	}
}
