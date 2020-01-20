package aya.instruction;

import java.util.ArrayList;
import java.util.LinkedList;

import aya.Aya;
import aya.obj.block.Block;
import aya.obj.block.BlockHeader;
import aya.variable.Variable;
import aya.variable.VariableData;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class BlockLiteralInstruction extends Instruction {
	
	Block _block;
	ArrayList<Variable> _captures;
	boolean _auto_eval;
	
	public BlockLiteralInstruction(Block b, ArrayList<Variable> captures) {
		if (captures.size() == 0) captures = null;
		_block = b;
		_captures = captures;
		_auto_eval = false;
	
		// If the block does not have a header but it has captures, create an empty one
		if (_captures != null) {
			BlockHeader bh = _block.getHeader();
			if (bh == null) {
				_block = _block.duplicate(new BlockHeader());
			}
		}
	}
	
	public BlockLiteralInstruction(Block b) {
		_block = b;
		_captures = null;
		_auto_eval = false;
	}
	
	/** Collect variables, return the Block object */
	public Block getBlock() {
		
		if (_captures == null) {
			return _block;
		} else {
			Block b = _block.duplicate();
			// Create a copy of the block header
			BlockHeader bh = b.getHeader().copy();
			b.getInstructions().replaceHeader(bh);

			VariableData vars = Aya.getInstance().getVars();
			for (Variable v : _captures) {
				bh.addDefault(v.getID(), vars.getVar(v.getID()));
			}
			
			return b;
		}
	}
	
	@Override
	public void execute(Block b) {
		Block blk = getBlock();
		if (_auto_eval) {
			b.addAll(blk.getInstructions().getInstrucionList());
		} else {
			b.push(blk);
		}
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		String s;
		if (_captures == null || _captures.size() == 0) {
			s = _block.str();
		} else {
			s = _block.strWithCaptures(_captures);
		}
		
		if (_auto_eval) {
			s = "(" + s + ")";
		}
		
		return s;
	}

	public void setAutoEval() {
		_auto_eval = true;
	}

	public boolean isRawBlock() {
		return _captures == null;
	}
	
	public Block getRawBlock() {
		return _block;
	}
}
