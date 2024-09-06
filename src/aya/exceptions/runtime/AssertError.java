package aya.exceptions.runtime;

import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class AssertError extends InternalAyaRuntimeException {

	private Obj _expected;
	private Obj _received;
	
	public AssertError(String msg, Obj expected, Obj recv) {
		super(SymbolConstants.ASSERT_ERROR, msg + ": \n  Expected: " 
											    + expected.repr(ReprStream.newSafe()) 
											    + "\n  Received: " 
											    + recv.repr(ReprStream.newSafe()));
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
