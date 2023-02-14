package aya.obj.list;

import aya.obj.Obj;

public class NDListIterator<O extends Obj> {
	List _list;
	int _ix;
	NDListIterator<O> _sub = null;
	boolean _loop = false;
	private final int _length;
	
	public NDListIterator(List out) {
		_list = out;
		_length = out.length();
		start();
	}
	
	public void setLoop(boolean b) {
		_loop = b;
	}
	
	public boolean done() {
		if (_loop) {
			return false;
		} else {
			return _ix >= _list.length();
		}
	}
	
	public void start() {
		_ix = 0;
	}

	@SuppressWarnings("unchecked")
	public O next() {
		if (_loop && _ix >= _length) {
			_ix = 0;
		}
		
		if (_sub != null) {
			O n = _sub.next();
			if (_sub.done()) {
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
					_sub = new NDListIterator<O>((List)val);
					return _sub.next();
				}
			} else {
				_ix++;
				return (O)(val);
			}
		}
	}
	
	public void setNext(O item) {
		if (_loop && _ix >= _length) {
			_ix = 0;
		}

		if (_sub != null) {
			_sub.setNext(item);
			if (_sub.done()) {
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
					_sub = new NDListIterator<O>((List)val);
					_sub.setNext(item);
				}
			} else {
				_list.mutSetExact(_ix++, item);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("done!");
	}

	

}



