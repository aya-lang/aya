package aya.obj.list;

import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.number.Num;
import aya.obj.number.Number;

public class NDListIterator <O extends Obj> {
	List _list;
	int _ix;
	NDListIterator<O> _sub = null;
	boolean _loop = false;
	
	public NDListIterator(List l) {
		_list = l;
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
		
		else if (_list.get(_ix).isa(Obj.LIST)) {
			_sub = new NDListIterator<O>((List)(_list.get(_ix)));
			if (_sub.done()) {
				// list was empty
				_ix++;
				return next();
			} else {
				return _sub.next();
			}
		}
		
		else {
			return (O)(_list.get(_ix++));
		}
	}
	
	public static void test() {
		
		ArrayList<Number> ns = new ArrayList<Number>();
		for (int i = 0; i < 3; i++) {
			ns.add(new Num(i));
		}
		
		NumberItemList nil = new NumberItemList(ns);
		
		System.out.println(nil.repr());
		
		NDListIterator<Number> iter = new NDListIterator<>(nil);
		
		//while (!iter.done()) {
			//System.out.println(iter.next());
		//}
		
		ArrayList<Obj> os = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			os.add(nil.mul(new Num(i+1)).deepcopy());
		}
		
		GenericList gl = new GenericList(os);
		
		System.out.println(gl.repr());
		
		iter = new NDListIterator<>(gl);
		
		while (!iter.done()) {
			System.out.println(iter.next());
		}
		
		gl.set(1, gl.deepcopy());
		System.out.println(gl.repr());

		iter = new NDListIterator<>(gl);
		while (!iter.done()) {
			System.out.println(iter.next());
		}
		
		System.out.println(nil);
		iter = new NDListIterator<Number>(nil);
		iter.setLoop(true);
		for (int i = 0; i < 10; i++) {
			System.out.println(iter.next());
		}
		
		
	}
	
	public static void main(String[] args) {
		test();
		System.out.println("done!");
	}

}



