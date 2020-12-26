package test.obj.number;

import aya.obj.number.FractionNum;
import test.Test;

public class FractionNumTest extends Test {

	@Override
	public void runTests() {
		Test.eq(new FractionNum(1, 2).str(), ":1r2", "FractionNum.str()");
		Test.eq(new FractionNum(3L, 6L).str(), ":1r2", "FractionNum.str() reduce");
		Test.eq(new FractionNum(1.75), new FractionNum(7, 4), "FractionNum(double)");
		Test.eq(new FractionNum(1, 2).ceil(), new FractionNum(1,1), "FractionNum.ceil()");
		Test.eq(new FractionNum(1, 2).floor(), new FractionNum(0,1), "FractionNum.floor()");
		Test.eq(new FractionNum(0, 8).toDouble(), 0.0, "FractionNum(0,8) == 0.0");
		Test.eq(new FractionNum(11,1).isPrime(), true, "FractionNum().isPrime()");
		Test.eq(new FractionNum(22,2).isPrime(), true, "FractionNum().isPrime()");
		Test.eq(new FractionNum(-3,4).negate(), new FractionNum(-3,-4), "FractionNum().negate()");
		Test.eq(new FractionNum(0.9651).sin().toDouble(), Math.sin(0.9651), "FractionNum().sin()");
		
		System.out.println("FractionNumTest: all tests passed!");
	}
	
	public static void main(String[] args) {
		(new FractionNumTest()).runTests();
	}

}
