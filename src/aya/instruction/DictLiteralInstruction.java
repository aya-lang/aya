package aya.instruction;

import java.util.HashMap;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.EmptyStackError;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.variable.VariableData;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class DictLiteralInstruction extends Instruction {
	
	StaticBlock _block;
	HashMap<Symbol, StaticBlock> _defaults;
	
	public DictLiteralInstruction(SourceStringRef source, StaticBlock b) {
		this(source, b, null);
	}
	
	public DictLiteralInstruction(SourceStringRef source, StaticBlock b, HashMap<Symbol, StaticBlock> defaults) {
		super(source);
		this._block = b;
		this._defaults = defaults;
	}

	@Override
	public void execute(BlockEvaluator b) {
		final VariableData vars = b.getContext().getVars();
		
		// Add the variable set to the stack, true: capture all assignments
		Dict dict_scope = new Dict();
		vars.add(dict_scope, true);
		
		// If there are defaults, add them to the dict scope
		if (_defaults != null) {
			for (Symbol var : _defaults.keySet()) {
				BlockEvaluator evaluator = b.getContext().createEvaluator();
				evaluator.dump(_defaults.get(var));
				evaluator.eval();
				dict_scope.set(var, evaluator.pop());
			}
		}
				
		// Run block in an isolated evaluator so we can copy the variables once the block is finished
		BlockEvaluator evaluator = b.getContext().createEvaluator();

		final int num_args = _block.getNumArgs();
		final boolean has_locals = _block.hasLocals();
		
		// Copy stack args into evaluator (preserve order)
		if (num_args > 0) {
			if (b.getStack().size() < num_args) {
				throw new EmptyStackError("Empty stack at dict literal");
			}
			final int offset   = b.getStack().size() - 1;
			for (int i = num_args-1; i >= 0; i--) evaluator.push(b.getStack().get(offset - i));
			for (int i = 0; i < num_args; i++) b.pop();
		}
		evaluator.dump(_block);

		// If we have locals, we will want to pull the data out if it before they are popped
		//   so we remove the instruction to pop the variables, we will do it ourselves below
		if (has_locals) evaluator.getInstructions().getInstrucionList().remove(0);
		
		evaluator.eval();
				
		if (has_locals) {
			Dict block_locals = vars.popGet();
			dict_scope.update(block_locals);
		}
		
		// Pop the dict scope and add it to the stack
		b.push(vars.popGet()); // popGet returns dict_scope
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(":");
		BlockUtils.repr(stream, _block, true, null, null);
		return stream;
	}
}
