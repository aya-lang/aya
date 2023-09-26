package aya.instruction;

import java.util.LinkedList;
import java.util.Queue;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.parser.SourceStringRef;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class DictLiteralInstruction extends Instruction {
	
	Block _block;
	private int num_captures;
	
	public DictLiteralInstruction(SourceStringRef source, Block b) {
		super(source);
		this._block = b;
		this.num_captures = 0;
	}
	
	public DictLiteralInstruction(SourceStringRef source, Block b, int num_captures) {
		super(source);
		this._block = b;
		this.num_captures = num_captures;
	}
	
	public int numCaptures() {
		return num_captures;
	}
	
	/** Run the dict, collect variables, return the Dict object */
	public Dict getDict(Queue<Obj> q) {
		//Add the variable set to the stack
		Aya.getInstance().getVars().add(new Dict(), true);
		
		//Run the block
		Block b2 = _block.duplicate();
		if (q != null) {
			while (!q.isEmpty()) {
				b2.push(q.poll());
			}
		}
		b2.eval();
		
		//Retrieve the Dict
		return Aya.getInstance().getVars().popGet();
	}
	
	@Override
	public void execute(Block b) {
		Queue<Obj> q = null;
		int n = this.numCaptures();
		if (n > 0) {
			q = new LinkedList<Obj>();
			for (int i = 0; i < n; i++) {
				q.add(b.pop());
			}
		}
		b.push(this.getDict(q));
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		if (num_captures == 0) {
			stream.print("{,");
		} else {
			stream.print("{" + num_captures + ",");
		}
		_block.repr(stream, false);
		stream.print("}");
		return stream;
	}
}
