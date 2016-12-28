package test.obj.number;

import element.obj.number.BigNum;
import element.obj.number.Num;
import test.Test;

public class NumberTest extends Test {

	@Override
	public void runTests() {
		Test.eq(new Num(0).bool(), false, "Num(0); bool");
		Test.eq(new Num(1).bool(), true, "Num(1); bool");
		Test.eq(new BigNum(0).bool(), false, "BigNum(0); bool");
		Test.eq(new BigNum(1).bool(), true, "BigNum(1); bool");

	}
	
	public static void main(String[] args) {
		new NumberTest().runTests();
	}

}
