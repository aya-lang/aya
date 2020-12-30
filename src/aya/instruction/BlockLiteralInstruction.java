package aya.instruction;

import java.util.ArrayList;

import aya.Aya;
import aya.ReprStream;
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
				bh.addDefault(v, vars.getVar(v));
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
	public ReprStream repr(ReprStream stream) {
		if (_auto_eval) stream.print("(");
		if (_captures == null || _captures.size() == 0) {
			_block.repr(stream);
		} else {
			_block.repr(stream, true, _captures);
		}
		if (_auto_eval) stream.print(")");
		return stream;
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
