package aya.instruction;

import java.util.ArrayList;
import java.util.LinkedList;

import aya.Aya;
import aya.obj.block.Block;
import aya.obj.block.BlockHeader;
import aya.obj.symbol.Symbol;
import aya.variable.VariableData;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class BlockLiteralInstruction extends Instruction {
	
	Block _block;
	ArrayList<Symbol> _captures;
	boolean _auto_eval;
	
	public BlockLiteralInstruction(Block b, ArrayList<Symbol> captures) {
		if (captures.size() == 0) captures = null;
		_block = b;
		_captures = captures;
		_auto_eval = false;
	
		// If the block does not have a header but it has captures, create an empty one
		if (_captures != null) {
			if (_block.getHeader() == null) {
				_block = _block.duplicateNewHeader(new BlockHeader());
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
			for (Symbol v : _captures) {
				bh.addDefault(v.id(), vars.getVar(v.id()));
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
