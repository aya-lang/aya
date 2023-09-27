package aya.exceptions.runtime;

import java.io.PrintStream;

import aya.exceptions.ex.AyaExceptionInterface;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceString;
import aya.parser.SourceStringRef;

/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public abstract class AyaRuntimeException extends RuntimeException implements AyaExceptionInterface {
	String msg;
	private SourceStringRef source;
	
	public AyaRuntimeException(String msg) {
		super(msg);
		this.msg = msg;
		this.source = null;
	}
	
	public AyaRuntimeException(Exception e) {
		super(e.getMessage());
		this.msg = e.getMessage();
		this.source = null;
	}

	public String getSimpleMessage() {
		return msg;
	}
	
	public abstract Symbol typeSymbol();
	
	public Dict getDict() {
		Dict d = new Dict();
		d.set(SymbolConstants.TYPE, typeSymbol());
		d.set(SymbolConstants.MSG, List.fromString(msg));
		if (this.source != null) {
			Dict dSource = new Dict();
			SourceString s = this.source.getSource();
			dSource.set(SymbolConstants.FILE, List.fromString(s.getFilename()));
			SourceString.IndexedSourceLine line = s.getIndexedLine(this.source.getIndex());
			dSource.set(SymbolConstants.LINE, Num.fromInt(line.lineNumber));
			dSource.set(SymbolConstants.COL, Num.fromInt(line.colNumber()));
			dSource.set(SymbolConstants.CONTEXT, List.fromString(this.source.getContextStr()));
			d.set(SymbolConstants.SOURCE, dSource);
		}
		return d;
	}

	public void setSource(SourceStringRef source) {
		// setSource may be called multiple times as the stack is unrolled
		// We only want the first call to actually set the source
		if (this.source == null) {
			this.source = source;
		}
	}
	
	public SourceStringRef getSource() {
		return this.source;
	}

	public void print(PrintStream stream) {
		stream.println(getMessage());
		if (this.source != null) {
			stream.println('\n');
			stream.println(this.source.getContextStr());
		}
	}
}
