package aya.obj.list;

import aya.obj.Obj;

public class NDListIterator <O extends Obj> {
	List _list;
	int _ix;
	NDListIterator<O> _sub = null;
	boolean _loop = false;
	
	public NDListIterator(List out) {
		_list = out;
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
		if (_loop && _ix >= _list.length()) {
			_ix = 0;
		}
		
		if (_sub != null) {
			O n = _sub.next();
			if (_sub.done()) {
				_sub = null;
				_ix++;
			}
			return n;
		}
		
		else if (_list.getExact(_ix).isa(Obj.LIST)) {
			_sub = new NDListIterator<O>((List)(_list.getExact(_ix)));
			if (_sub.done()) {
				// list was empty
				_ix++;
				return next();
			} else {
				return _sub.next();
			}
		}
		
		else {
			return (O)(_list.getExact(_ix++));
		}
	}
	
	public void setNext(O item) {
		if (_loop && _ix >= _list.length()) {
			_ix = 0;
		}
		
		if (_sub != null) {
			_sub.setNext(item);
			if (_sub.done()) {
				_sub = null;
				_ix++;
			}
			return;
		}
		
		else if (_list.getExact(_ix).isa(Obj.LIST)) {
			_sub = new NDListIterator<O>((List)(_list.getExact(_ix)));
			if (_sub.done()) {
				// list was empty
				_ix++;
				setNext(item);
				return;
			} else {
				_sub.setNext(item);
				return;
			}
		}
		
		else {
			_list.mutSetExact(_ix++, item);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("done!");
	}

	

}



