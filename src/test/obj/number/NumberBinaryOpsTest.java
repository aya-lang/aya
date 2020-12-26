package test.obj.number;

import aya.obj.number.FractionNum;
import aya.obj.number.Num;
import aya.obj.number.NumberMath;
import test.Test;

public class NumberBinaryOpsTest extends Test {

	@Override
	public void runTests() {
		Test.eq(NumberMath.add(new Num(0.5), new FractionNum(0.5)), FractionNum.ONE, "FractionNum add");

		System.out.println("NumberBinaryOpsTest: all tests passed!");
	}

	public static void main(String[] args) {
		new NumberBinaryOpsTest().runTests();
	}

}
