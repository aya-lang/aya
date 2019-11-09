package aya.instruction;

import java.util.LinkedList;
import java.util.Queue;

import aya.Aya;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.variable.VariableSet;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class DictLiteralInstruction extends Instruction {
	
	Block _block;
	private int num_captures;
	
	public DictLiteralInstruction(Block b) {
		this._block = b;
		this.num_captures = 0;
	}
	
	public DictLiteralInstruction(Block b, int num_captures) {
		this._block = b;
		this.num_captures = num_captures;
	}
	
	public int numCaptures() {
		return num_captures;
	}
	
	/** Run the dict, collect variables, return the Dict object */
	public Dict getDict(Queue<Obj> q) {
		
		//Capture all assignments within the scope
		VariableSet module = new VariableSet(true);
		
		//Add the variable set to the stack
		Aya.getInstance().getVars().add(module);
		
		//Run the block
		Block b2 = _block.duplicate();
		if (q != null) {
			while (!q.isEmpty()) {
				b2.push(q.poll());
			}
		}
		b2.eval();
		
		//Retrieve the variableSet
		module = Aya.getInstance().getVars().popGet();
		
		//Create a new dict using the assignments from the variable set
		return new Dict(module);
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
	protected String repr(LinkedList<Long> visited) {
		String bstr = _block.toString().substring(1);
		if (num_captures == 0) {
			return "{," + bstr;
		} else {
			return "{" + num_captures + "," + bstr;
		}
	}
}
