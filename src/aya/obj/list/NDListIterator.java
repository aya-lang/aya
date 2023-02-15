package aya.obj.list;

import aya.obj.Obj;

public class NDListIterator {
	private final List _list;
	private final boolean _loop;
	private final int _length;
	private final boolean _list_may_have_sublist;

	private int _ix;
	private NDListIterator _sub = null;

	
	public NDListIterator(List out) {
		this(out, false);
	}

	public NDListIterator(List out, boolean loop) {
		_loop = loop;
		_list = out;
		_length = out.length();
		if (_list.isa(Obj.NUMBERLIST) || _list.isa(Obj.STR)) {
			_list_may_have_sublist = false;
		} else {
			_list_may_have_sublist = true;
		}
		start();
	}
	
	public boolean done() {
		if (_loop) {
			return false;
		} else {
			return _ix >= _length;
		}
	}

	public boolean doneNoCheckLoop() {
		return _ix >= _length;
	}
	
	public void start() {
		_ix = 0;
	}

	public void skip() {
		if (_list_may_have_sublist) {
			if (_sub != null) {
				_sub.skip();
				if (_sub.doneNoCheckLoop()) {
					_sub = null;
					_ix++;
				}
			} else {
				next();
			}
		} else {
			_ix++;
		}
	}

	public Obj next() {
		if (_loop && _ix >= _length) {
			_ix = 0;
		}
		
		if (_list_may_have_sublist) {
			if (_sub != null) {
				Obj n = _sub.next();
				if (_sub.doneNoCheckLoop()) {
					_sub = null;
					_ix++;
				}
				return n;
			} else {
				Obj val = _list.getExact(_ix);
				if (val.isa(Obj.LIST)) {
					List sub = (List)val;
					if (sub.length() == 0) {
						// List was empty
						_ix++;
						return next();
					} else {
						_sub = new NDListIterator((List)val);
						return _sub.next();
					}
				} else {
					_ix++;
					return val;
				}
			}
		} else {
			return _list.getExact(_ix++);
		}
	}
	
	public void setNext(Obj item) {
		if (_loop && _ix >= _length) {
			_ix = 0;
		}

		if (_list_may_have_sublist) {
			if (_sub != null) {
				_sub.setNext(item);
				if (_sub.doneNoCheckLoop()) {
					_sub = null;
					_ix++;
				}
			} else {
				Obj val = _list.getExact(_ix);
				if (val.isa(Obj.LIST)) {
					List sub = (List)val;
					if (sub.length() == 0) {
						// List was empty
						_ix++;
						setNext(item);
					} else {
						_sub = new NDListIterator((List)val);
						_sub.setNext(item);
					}
				} else {
					_list.mutSetExact(_ix++, item);
				}
			}
		} else {
			_list.mutSetExact(_ix++, item);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("done!");
	}

	

}



