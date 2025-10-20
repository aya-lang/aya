package aya.exceptions.runtime;

import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class AssertError extends InternalAyaRuntimeException {

	private Obj _expected;
	private Obj _received;

	private static ReprStream getReprStream() {
		ReprStream reprStream = ReprStream.newSafe();
		reprStream.setFullStrings(true); // do not truncate text in assertion errors
		return reprStream;
	}

	public AssertError(String msg, Obj expected, Obj recv) {
		super(SymbolConstants.ASSERT_ERROR, msg + ": \n  Expected: "
				+ expected.repr(getReprStream())
				+ "\n  Received: "
				+ recv.repr(getReprStream()));
		_expected = expected;
		_received = recv;
	}

	@Override
	public Dict getDict() {
		Dict d = super.getDict();
		d.set(SymbolConstants.EXPECTED, _expected);
		d.set(SymbolConstants.RECEIVED, _received);
		return d;
	}

}
