package aya.variable;

/**
 * Variable class. Stores variable names as a unique single long value
 * for faster comparison. The string must be all lower case letters
 * and only the first 12 characters are used.
 * @author Nick
 */
public class Variable {
	protected long id;
	protected boolean referanceVariable = true;
	
	private static final char UNDERSCORE  = 'z' + 1; 
	private static final char TERMINATION = 'z' + 2;
	
	private static final char ENCODED_UNDERSCORE  = UNDERSCORE - 'a';
	private static final char ENCODED_TERMINATION = TERMINATION - 'a';

	/** Creates a variable object using a string.
	 *  Only the first 12 characters of the string are used
	 */
	public Variable(String s) {
		id = encodeString(s);
	}
	
	/** Create a new variable given its id */
	public Variable(long id) {
		this.id = id;
	}

	/** Returns the id for the variable */
	public long getID() {
		return id;
	}
	
	/**Sets the bind flag to true. The variable will be bound
	 * instead of referenced at runtime */
	public void flagBind() {
		referanceVariable = false;
	}
	
	/**Returns true if the variable is should be bound,
	 * returns false if the variable is a reference */
	public boolean shouldBind() {
		return !referanceVariable;
	}
	
	/**
	 * Converts a long into a string of lower case letters
	 * @param l
	 * @return
	 */
	public static String decodeLong(long l) {
		StringBuilder sb = new StringBuilder();		
		for (int i = 0; i < 12; i++) {
			// (l >> (5*i) & 0xff) Extract the ith five bits from the long
			// & (long)(~(1<<5)) Set the 6th-8th bits to 0 ensuring that we are only reading 5 bits
			char c = (char)('a'+
					((l >> (5*i) & 0xff) & (long)(~(1<<5)) & (long)(~(1<<6)) & (long)(~(1<<7)))
					);
		
			//Early termination character
			if(c == TERMINATION) {
				break;
			} else if (c == UNDERSCORE) {
				sb.append('_');
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Converts a string of lower case letters (a-z) to a unique long. Only
	 * the first 12 characters of the string are used.
	 * @param s
	 * @return
	 */
	public static long encodeString(String s) {
		char[] chars = s.toCharArray();
		
		//Only use the first 12 characters of the string
		int loops = 12;
		if(chars.length < 12) {
			loops = chars.length;
		}
		
		long res = 0L;
		int i;
		for (i = 0; i < loops; i++) {
			// Convert underscore
			if (chars[i] == '_') chars[i] = UNDERSCORE;
			//Shift the 5 bits (a-z and the termination char) into the long
			long alpha = ((long)(chars[i]-'a')) << ((long)i*5);
			//Combine the 2 longs using or
			res =  res | alpha;
		}
		
		//Add the early termination character
		if(chars.length < 12) {
			long alpha = (long)(ENCODED_TERMINATION) << ((long)(i)*5);
			res = ((long)res) | ((long)alpha);
			
		}
		
		return res;
	}
	
	/** Return true if the string is a valid variable name */
	public static boolean isValidStr(String varname) {
		if (varname.length() >= 12) return false;
		
		char[] chars = varname.toCharArray();
		for (char c : chars) {
			if (!isValidChar(c)) return false;
		}
		
		return true;
	}
	
	public static boolean isValidChar(char c) {
		return (c >= 'a' && c <= 'z') || c == '_';
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.id == ((Variable)obj).getID();
	}
	
	@Override
	public String toString() {
		String name = decodeLong(id);
		if (referanceVariable) {
			return name;
		} else {
			return ":" + name;
		}
	}
	
}
