package aya.exceptions.runtime;

import java.io.IOException;

import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class IOError extends InternalAyaRuntimeException {

	public IOError(String operator_name, String resource, IOException reason) {
		this(operator_name, resource, reason.getMessage());
	}

	public IOError(String operator_name, String resource, String reason) {
		super(SymbolConstants.IO_ERR, "io_err at " + operator_name + ": unable to use resource " + resource + ". " + reason);
	}

}
