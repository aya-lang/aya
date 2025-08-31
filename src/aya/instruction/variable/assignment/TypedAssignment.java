package aya.instruction.variable.assignment;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.op.OperatorInstruction;
import aya.instruction.op.Ops;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;
import aya.util.Casting;
import aya.util.TypeUtils;

public class TypedAssignment extends CopyAssignment {

	// type may be null
	// if null, assume "any" and don't check type
	protected Dict _type;
	
	// The unparsed type information
	// When when a block with this argument is defined, we need to evaluate this
	// and convert it to the type dict
	protected StaticBlock _type_block;
	
	public TypedAssignment(SourceStringRef source, Symbol var, Dict type, StaticBlock type_block, boolean copy) {
		super(source, var, copy);
		_type = type;
		_type_block = type_block;
	}
	
	public TypedAssignment(SourceStringRef source, Symbol var, StaticBlock type, boolean copy) {
		this(source, var, null, type, copy);
	}
	
	public void assign(Dict vars, Obj o, ExecutionContext ctx) {	
		// if type is null, assume "any"
		if (_type == null || TypeUtils.isInstance(o, _type, ctx)) {
			super.assign(vars, o, ctx);
		} else {
			TypeError e = new TypeError("Type error at argument: " + this.toString() + "\n\tExpected type: " + _type.repr()
						+ "\n\tReceived: " + o);
			e.setSource(getSource());
			throw e;
		}
	}

	@Override
	public Assignment setTypeInfo(ExecutionContext ctx) {
		if (_type_block == null) {
			return this;
		} else {
			BlockEvaluator be = ctx.createEvaluator();
			if (_type_block.isLastInstructionListLiteral()) {
				be.add(new OperatorInstruction(_source, Ops.OP_T_MAKE_TYPE));
			}
			_type_block.dumpToBlockEvaluator(be);
			be.eval();
			Obj res = be.pop();
			if (res.isa(Obj.DICT)) {
				// Return a copy
				return new TypedAssignment(_source, _var, Casting.asDict(res), _type_block, _copy);
			} else {
				throw new ValueError("Type must be a dict");
			}
		}
	}
	
	@Override
	public boolean hasTypeInfo() {
		return true;
	}
	
	
	@Override
	public String toString() {
		if (_type_block != null) {
			ReprStream s = new ReprStream();
			s.setTight(true);
			s.print(super.toString());
			s.print("::");
			BlockUtils.repr(s, _type_block, false, null);
			return s.toStringOneline();
		} else {
			return "::any";
		}
	}

	
	@Override
	public void toDict(Dict d) {
		super.toDict(d);
		d.set(SymbolConstants.TYPE, _type);
	}

}
