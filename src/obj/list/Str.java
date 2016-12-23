package obj.list;

import obj.Obj;

public class Str extends List implements Comparable<Str> {
	
	private String _str;
	
	public String getStr() {
		return _str;
	}
		
	public Str(String s) {
		_str = s;
	}
	
	
	///////////////////////
	// STRING OPERATIONS //
	///////////////////////
	
	/** Trim whitespace from a string */
	public Str trim() {
		// TODO
		return new Str("");
	}
	
	/** replace all occurrences of 'find' with 'replace' */
	public Str replaceAll(String find, String replace) {
		//TODO
		return new Str("");
	}
	
	/** Test if Str matches the regex */
	public boolean matches(String regex) {
		//TODO
		return false;
	}
	
	/** Apply format rules to the string */
	public Str format(Obj... objs) {
		//TODO
		return new Str("");
	}
	
	
	
	
	////////////////////
	// LIST OVERRIDES //
	////////////////////

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void head(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tail(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public Obj head() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj tail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj pop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj popBack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void slice(int i, int j) {
		// TODO Auto-generated method stub

	}

	@Override
	public Obj get(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int find(Obj o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int findBack(Obj o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count(Obj o) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean bool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String repr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String str() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equiv(Obj o) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	////////////////
	// COMPARABLE //
	////////////////
	
	@Override
	public int compareTo(Str o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
