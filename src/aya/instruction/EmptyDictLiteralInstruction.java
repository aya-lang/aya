package aya.instruction;

import java.util.Queue;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;

/** Specialization of a DictLiteralInstruction which always returns an empty dict
 * @author Nick
 *
 */
public class EmptyDictLiteralInstruction extends DictLiteralInstruction {
	
	public static final EmptyDictLiteralInstruction INSTANCE = new EmptyDictLiteralInstruction();
	
	protected EmptyDictLiteralInstruction() {
		super(null, new Block());
	}
	
	@Override
	public int numCaptures() { return 0; }
	
	@Override
	public Dict getDict(Queue<Obj> q) {
		return new Dict();
	}
	
	public Dict getDict() {
		return new Dict();
	}
	
	@Override
	public String toString() {
		return "{,}";
	}
}
