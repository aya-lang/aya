package test.obj.list.numberlist;

import java.util.ArrayList;

import obj.list.numberlist.NumberItemList;
import obj.number.Num;
import obj.number.Number;
import test.Test;

public class NumberItemListTest extends Test {
	
	private static NumberItemList ls(Double... doubles) {
		ArrayList<Number> ns = new ArrayList<Number>(doubles.length);
		for (Double d : doubles) {
			ns.add(new Num(d));
		}
		return new NumberItemList(ns);
	}
	
	private static NumberItemList ls(Integer...ints) {
		ArrayList<Number> ns = new ArrayList<Number>(ints.length);
		for (Integer i : ints) {
			ns.add(new Num(i));
		}
		return new NumberItemList(ns);
	}
	
	private static NumberItemList ls() {
		return new NumberItemList(new ArrayList<Number>(0));
	}
	
	private static Num num(int i) {
		return new Num(i);
	}
	private static Num num(double d) {
		return new Num(d);
	}
	
	@Override
	public void runTests() {

		NumberItemList oneSix = ls(1,2,3,4,5,6);
		
		// Head / Tail
		Test.eq(oneSix.head(), num(1) , "head");
		Test.eq(oneSix.head(2), ls(1, 2), "head(2)");
		Test.eq(oneSix.tail(), num(6) , "tail");
		Test.eq(oneSix.tail(2), ls(5, 6), "tail(2)");
		Test.eq(oneSix.head(8), ls(1,2,3,4,5,6,0,0), "head overtake");
		Test.eq(oneSix.tail(8), ls(0,0,1,2,3,4,5,6), "tail overtake");
		Test.eq(oneSix.head(6), ls(1,2,3,4,5,6), "head exact");
		Test.eq(oneSix.tail(6), ls(1,2,3,4,5,6), "tail exact");
		Test.eq(oneSix.head(0), ls(), "head zero");
		Test.eq(oneSix.tail(0), ls(), "tail zero");
		Test.eq(oneSix.head(-2), ls(1,2,3,4), "head neg");
		Test.eq(oneSix.tail(-2), ls(3,4,5,6), "tail neg");
		
		// Stats
		Test.eq(oneSix.sum(), num(21), "sum");
		Test.eq(ls().sum(), num(0), "sum zero");
		Test.eq(oneSix.min(), num(1), "min");
		Test.eq(oneSix.max(), num(6), "max");
		Test.eq(ls().min(), Num.MAX_VALUE, "min empty");
		Test.eq(ls().max(), Num.MIN_VALUE, "max empty");
		Test.eq(oneSix.mean(), num(3.5), "mean");
		Test.eq(ls().mean(), num(0), "mean empty");
		
		//Math
		ArrayList<Number> sinOneSix_AL = new ArrayList<Number>(6);
		ArrayList<Number> cosOneSix_AL = new ArrayList<Number>(6);
		ArrayList<Number> tanOneSix_AL = new ArrayList<Number>(6);
		ArrayList<Number> logOneSix_AL = new ArrayList<Number>(6);
		ArrayList<Number> lnOneSix_AL = new ArrayList<Number>(6);
		ArrayList<Number> sqrtOneSix_AL = new ArrayList<Number>(6);
		for (double i = 1.0; i <= 6.0; i++) {
			sinOneSix_AL.add(num(Math.sin(i)));
			cosOneSix_AL.add(num(Math.cos(i)));
			tanOneSix_AL.add(num(Math.tan(i)));
			logOneSix_AL.add(num(Math.log10(i)));
			lnOneSix_AL.add(num(Math.log(i)));
			sqrtOneSix_AL.add(num(Math.sqrt(i)));
		}
		NumberItemList sinOneSix  = new NumberItemList(sinOneSix_AL);
		NumberItemList cosOneSix  = new NumberItemList(cosOneSix_AL);
		NumberItemList tanOneSix  = new NumberItemList(tanOneSix_AL);
		NumberItemList logOneSix  = new NumberItemList(logOneSix_AL);
		NumberItemList lnOneSix   = new NumberItemList(lnOneSix_AL);
		NumberItemList sqrtOneSix = new NumberItemList(sqrtOneSix_AL);
		
		Test.eq(oneSix.deepcopy().sin() , sinOneSix , "sin" );
		Test.eq(oneSix.deepcopy().cos() , cosOneSix , "cos" );
		Test.eq(oneSix.deepcopy().tan() , tanOneSix , "tan" );
		Test.eq(oneSix.deepcopy().log() , logOneSix , "log" );
		Test.eq(oneSix.deepcopy().ln()  , lnOneSix  , "ln"  );
		Test.eq(oneSix.deepcopy().sqrt(), sqrtOneSix, "sqrt");
		
		// Binary Math
		
		
		
		System.out.println("NumberItemListTest: all tests passed!");
	}
	
	public static void main(String[] args) {
		new NumberItemListTest().runTests();
	}
	
}
