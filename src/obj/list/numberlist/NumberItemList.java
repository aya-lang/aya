package obj.list.numberlist;

import java.util.ArrayList;

import obj.Obj;
import obj.number.Number;

public class NumberItemList extends NumberList {
	
	ArrayList<Number> _list;

	@Override
	public Number max() {
		Number max = _list.get(0);
		for (int i = 1; i < _list.size(); i++) {
			if (_list.get(i).compareTo(max) > 0) {
				max = _list.get(i);
			}
		}
		return max;
	}

	@Override
	public Number min() {
		Number min = _list.get(0);
		for (int i = 1; i < _list.size(); i++) {
			if (_list.get(i).compareTo(min) < 0) {
				min = _list.get(i);
			}
		}
		return min;
	}

	@Override
	public Number mean() {
		// TODO
		return null;
	}

	@Override
	public Number sum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList add(Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList sub(Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList div(Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList mul(Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList mod(Number n) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public NumberList idiv(Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList pow(Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList bnot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList signnum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList factorial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList abs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList sin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList cos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList tan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList asin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList acos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList atan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList log() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList ln() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList sqrt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList ceil() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberList floor() {
		// TODO Auto-generated method stub
		return null;
	}

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
	
	@Override
	public boolean isa(byte type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte type() {
		// TODO Auto-generated method stub
		return 0;
	}



}
