package aya.parser;

public class SourceStringRef {

	private SourceString source;
	private int idx;
	
	SourceStringRef(SourceString source, int idx) {
		this.source = source;
		this.idx = idx;
	}
	
	public String getContextStr() {
		return this.source.getContextStr(this.idx);
	}
	
	public int getIndex() {
		return this.idx;
	}

	public SourceString getSource() {
		return this.source;
	}
	
	@Override
	public String toString() {
		return this.getContextStr();
	}
}
