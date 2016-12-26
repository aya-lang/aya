package test.obj.list;

import obj.character.Char;
import obj.list.Str;
import test.Test;

public class StrTest extends Test {

	public static Str s(String str) {
		return new Str(str);
	}
	@Override
	public void runTests() {
		Str oneSix = new Str("123456");
				
		// Head / Tail
		Test.eq(oneSix.head(),  Char.valueOf('1') , "head");
		Test.eq(oneSix.head(2), s("12"), "head(2)");
		Test.eq(oneSix.tail(),  Char.valueOf('6'), "tail");
		Test.eq(oneSix.tail(2), s("56"), "tail(2)");
		Test.eq(oneSix.head(8), s("123456  "), "head overtake");
		Test.eq(oneSix.tail(8), s("  123456"), "tail overtake");
		Test.eq(oneSix.head(6), s("123456"), "head exact");
		Test.eq(oneSix.tail(6), s("123456"), "tail exact");
		Test.eq(oneSix.head(0), s(""), "head zero");
		Test.eq(oneSix.tail(0), s(""), "tail zero");
		Test.eq(oneSix.head(-2),s("1234"), "head neg");
		Test.eq(oneSix.tail(-2),s("3456"), "tail neg");
		
		//In-place reverse
		oneSix.reverse();
		Test.eq(oneSix, s("654321"), "reverse");
		oneSix.reverse();
		
		System.out.println("StrTest: all tests passed!");
	}

	public static void main(String[] args) {
		new StrTest().runTests();

	}

}
