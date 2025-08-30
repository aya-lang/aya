package aya.instruction;

import java.util.HashMap;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class BlockLiteralInstruction extends Instruction {
		
	StaticBlock _block;
	HashMap<Symbol, StaticBlock> _defaults;
	boolean _auto_eval;
	
	public BlockLiteralInstruction(SourceStringRef source, StaticBlock b, HashMap<Symbol, StaticBlock> defaults) {
		super(source);
		if (defaults != null && defaults.size() == 0) _defaults = null;
		_block = b;
		_defaults = defaults;
		_auto_eval = false;
	
		// If the instruction has locals, make sure the underlying blockEvaluator has them as well
		if (_defaults != null) {
			_block = BlockUtils.addLocals(_block);
		}
	}
	
	public BlockLiteralInstruction(SourceStringRef source, StaticBlock b) {
		super(source);
		_block = b;
		_defaults = null;
		_auto_eval = false;
	}
	
	
	@Override
	public void execute(BlockEvaluator b) {
		StaticBlock blk = _block;
		
		// Set type info for arguments
		blk = BlockUtils.copySetTypeInfo(blk, b.getContext());

		// If there are defaults, evaluate them and push a new blockEvaluator
		if (_defaults != null) {
			Dict defaults = new Dict();
			for (Symbol var : _defaults.keySet()) {
				BlockEvaluator evaluator = b.getContext().createEvaluator();
				evaluator.dump(_defaults.get(var));
				evaluator.eval();
				defaults.set(var, evaluator.pop());
			}
			blk = BlockUtils.mergeLocals(blk, defaults);
		}
		
		
		if (_auto_eval) {
			b.dump(blk);
		} else {
			b.push(blk);
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		if (_auto_eval) stream.print("(");
		BlockUtils.repr(stream, _block, true, _defaults);
		if (_auto_eval) stream.print(")");
		return stream;
	}

	public void setAutoEval() {
		_auto_eval = true;
	}

	public StaticBlock getRawBlock() {
		return _block;
	}
}
