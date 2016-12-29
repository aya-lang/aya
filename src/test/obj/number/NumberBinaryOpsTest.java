package test.obj.number;

import element.obj.number.Num;
import element.obj.number.RationalNum;
import test.Test;

public class NumberBinaryOpsTest extends Test {

	@Override
	public void runTests() {
		Test.eq(new RationalNum(0.5).add(new Num(0.5)), new RationalNum(1,1), "RationalNum.add");

		System.out.println("NumberBinaryOpsTest: all tests passed!");
	}

	public static void main(String[] args) {
		new NumberBinaryOpsTest().runTests();
	}

}
