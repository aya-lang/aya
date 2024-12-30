package aya.instruction;

import java.util.LinkedList;
import java.util.Queue;

import aya.ReprStream;
import aya.eval.ExecutionContext;
import aya.eval.BlockEvaluator;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.parser.SourceStringRef;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class DictLiteralInstruction extends Instruction {
	
	StaticBlock _block;
	private int num_captures;
	
	public DictLiteralInstruction(SourceStringRef source, StaticBlock b) {
		super(source);
		this._block = b;
		this.num_captures = 0;
	}
	
	public DictLiteralInstruction(SourceStringRef source, StaticBlock b, int num_captures) {
		super(source);
		this._block = b;
		this.num_captures = num_captures;
	}
	
	public int numCaptures() {
		return num_captures;
	}
	
	/** Run the dict, collect variables, return the Dict object */
	public Dict getDict(ExecutionContext context, Queue<Obj> q) {
		//Add the variable set to the stack
		context.getVars().add(new Dict(), true);
		
		//Run the blockEvaluator
		BlockEvaluator evaluator = context.createEvaluator();
		if (q != null) {
			while (!q.isEmpty()) {
				evaluator.push(q.poll());
			}
		}
		evaluator.dump(_block);
		evaluator.eval();
		
		//Retrieve the Dict
		return context.getVars().popGet();
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		Queue<Obj> q = null;
		int n = this.numCaptures();
		if (n > 0) {
			q = new LinkedList<Obj>();
			for (int i = 0; i < n; i++) {
				q.add(b.pop());
			}
		}
		b.push(this.getDict(b.getContext(), q));
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		if (num_captures == 0) {
			stream.print(":{");
		} else {
			stream.print("{" + num_captures + ",");
		}
		BlockUtils.repr(stream, _block, false, null);
		stream.print("}");
		return stream;
	}
}
