package aya.obj.dict;

import java.util.Queue;

import aya.Aya;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.variable.VariableSet;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class DictFactory {
	Block b;
	private int num_captures;
	
	public DictFactory(Block b) {
		this.b = b;
		this.num_captures = 0;
	}
	
	public DictFactory(Block b, int num_captures) {
		this.b = b;
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
		Block b2 = b.duplicate();
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
	public String toString() {
		String bstr = b.toString().substring(1);
		if (num_captures == 0) {
			return "{," + bstr;
		} else {
			return "{" + num_captures + "," + bstr;
		}
	}
}
