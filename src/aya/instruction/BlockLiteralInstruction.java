package aya.instruction;

import java.util.HashMap;

import aya.ReprStream;
import aya.obj.block.Block;
import aya.obj.block.BlockHeader;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class BlockLiteralInstruction extends Instruction {
	
	Block _block;
	//ArrayList<Symbol> _captures;
	HashMap<Symbol, Block> _defaults;
	boolean _auto_eval;
	
	public BlockLiteralInstruction(SourceStringRef source, Block b, HashMap<Symbol, Block> defaults) {
		super(source);
		if (defaults.size() == 0) _defaults = null;
		_block = b;
		_defaults = defaults;
		_auto_eval = false;
	
		// If the block does not have a header but it has captures, create an empty one
		if (_defaults != null) {
			if (_block.getHeader() == null) {
				_block = _block.duplicateNewHeader(new BlockHeader(this.getSource()));
			}
		}
	}
	
	public BlockLiteralInstruction(SourceStringRef source, Block b) {
		super(source);
		_block = b;
		_defaults = null;
		_auto_eval = false;
	}
	
	/** Collect variables, return the Block object */
	public Block getBlock() {
		
		if (_defaults == null) {
			return _block;
		} else {
			Block b = _block.duplicate();
			// Create a copy of the block header
			BlockHeader bh = b.getHeader().copy();
			b.getInstructions().replaceHeader(bh);

			for (Symbol var : _defaults.keySet()) {
				Block dflt = _defaults.get(var).duplicate();
				dflt.eval();
				bh.addDefault(var, dflt.pop());
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
		if (_defaults == null || _defaults.size() == 0) {
			_block.repr(stream);
		} else {
			_block.repr(stream, true, _defaults);
		}
		if (_auto_eval) stream.print(")");
		return stream;
	}

	public void setAutoEval() {
		_auto_eval = true;
	}

	public Block getRawBlock() {
		return _block;
	}
}
