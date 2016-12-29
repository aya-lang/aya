package test.obj.number;

import element.obj.number.RationalNum;
import test.Test;

public class RationalNumTest extends Test {

	@Override
	public void runTests() {
		Test.eq(new RationalNum(1, 2).str(), ":1r2", "RationalNum.str()");
		Test.eq(new RationalNum(3L, 6L).str(), ":1r2", "RationalNum.str() reduce");
		Test.eq(new RationalNum(1.75), new RationalNum(7, 4), "RationalNum(double)");
		Test.eq(new RationalNum(1, 2).ceil(), new RationalNum(1,1), "RationalNum.ceil()");
		Test.eq(new RationalNum(1, 2).floor(), new RationalNum(0,1), "RationalNum.floor()");
		Test.eq(new RationalNum(0, 8).toDouble(), 0.0, "RationalNum(0,8) == 0.0");
		Test.eq(new RationalNum(11,1).isPrime(), true, "RationalNum().isPrime()");
		Test.eq(new RationalNum(22,2).isPrime(), true, "RationalNum().isPrime()");
		Test.eq(new RationalNum(-3,4).negate(), new RationalNum(-3,-4), "RationalNum().negate()");
		Test.eq(new RationalNum(0.9651).sin().toDouble(), Math.sin(0.9651), "RationalNum().sin()");
		
		System.out.println("RationalNumTest: all tests passed!");
	}
	
	public static void main(String[] args) {
		(new RationalNumTest()).runTests();
	}

}
