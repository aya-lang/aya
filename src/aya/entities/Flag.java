package aya.entities;

/**
 * Flags are special instructions that get passed to the interpreter.
 * Flags cannot be called from within the syntax and are generated
 * exclusively by the compiler.
 * @author npaul
 *
 */
public class Flag {
	public static final byte NULL = 0;
	public static final byte POPVAR = 1;
	public static final byte EVAL_BLOCK = 2;
	public static final byte QUOTE_FUNCTION = 3;

	/** No need to create new flag objects every time,
	 *  just reference the table when you need a new flag
	 */
	private static final Flag[] FLAGTABLE = {
		new Flag(NULL),
		new Flag(POPVAR),
		new Flag(EVAL_BLOCK),
		new Flag(QUOTE_FUNCTION)
	};

	
	
	/** The flag's id */
	byte id;

	/** Creates a new flag with the given type */
	public Flag(byte b) {
		this.id = b;
	}
	
	
	/** Returns a flag from the flag table */
	public static Flag getFlag(byte b) {
		return FLAGTABLE[b];
	}
	
	/** Return's the flag's id */
	public byte getID() {
		return id;
	}
	
	@Override
	public String toString() {
		if (id == QUOTE_FUNCTION) {
			return ".`";
		} else {
			return "";
		}
	}
}
