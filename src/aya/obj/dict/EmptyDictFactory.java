package aya.obj.dict;

import java.util.Queue;

import aya.obj.Obj;
import aya.obj.block.Block;

/** Specialization of a DictFactory which always returns an empty dict
 * @author Nick
 *
 */
public class EmptyDictFactory extends DictFactory {
	
	public static final EmptyDictFactory INSTANCE = new EmptyDictFactory();
	
	protected EmptyDictFactory() {
		super(new Block());
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
