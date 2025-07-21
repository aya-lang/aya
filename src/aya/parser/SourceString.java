package aya.parser;

public class SourceString {
	private String source;
	private String filename;
	private String[] lines;
	
	public static class IndexedSourceLine {
		public String line;
		public int index;
		public int lineIndex;
		public int lineNumber;
		
		public IndexedSourceLine(String line, int index, int lineIndex) {
			this.line = line;
			this.index = index;
			this.lineIndex = lineIndex;
			this.lineNumber = lineIndex+1;
		}

		public String pointerStr() {
			String s  = "";
			for (int i = 0; i < this.index; i++) s += "~";
			return s + "^";
		}
		
		public int colNumber() {
			return this.index + 1;
		}
		
		@Override
		public String toString() {
			return "IndexedSourceLine(line " + this.lineIndex + ", col " + this.index + ":\n" + this.line + "\n)";
		}
	};
	
	public SourceString(String source, String filename) {
		this.source = source;
		this.filename = filename;
		this.lines = source.split("\n");
	}
	
	public int length() {
		return this.source.length();
	}
	
	public String getSource() {
		return this.source;
	}
	public String getFilename() {
		return this.filename;
	}
	
	public IndexedSourceLine getIndexedLine(int charIndex) {
		if (this.length() == 0) {
			return new IndexedSourceLine("", 0, 0);
		} else {
			StringBuilder line = new StringBuilder();
			int idx = charIndex;
			// Forward
			char c = getChar(idx);
			while (c != '\n') {
				line.append(c);
				idx++;
				if (idx > this.source.length()-1) break;
				c = getChar(idx);
			}
			// Backward
			int indexInLine = 0;
			idx = charIndex-1;
			if (idx >= 0) {
				c = getChar(idx);
				while (c != '\n') {
					line.insert(0, c);
					idx--;
					indexInLine++;
					if (idx < 0) break;
					c = getChar(idx);
				}
			}
			return new IndexedSourceLine(line.toString(), indexInLine, this.getLineIndex(charIndex));
		}
	}
	
	public char getChar(int charIndex) {
		return this.source.charAt(charIndex);
	}
	
	public String getRawString() {
		return this.source;
	}
	
	// Zero indexed, use getLineNumber for line number
	public int getLineIndex(int charIndex) {
		int lineCount = 0;
		if (this.length() > 0) {
			for (int i = 0; i < charIndex; i++) {
				if (this.source.charAt(i) == '\n') lineCount++;
			}
		}
		return lineCount;
	}
	
	public int getLineNumber(int charIndex) {
		return getLineIndex(charIndex) + 1;
	}
	
	
	public int getLineCharNumber(int charIndex) {
		IndexedSourceLine isl = this.getIndexedLine(charIndex);
		return isl.index + 1;
	}

	
	public String getContextStr(int charIndex) {
		StringBuilder sb = new StringBuilder();
		IndexedSourceLine line = this.getIndexedLine(charIndex);
		
		if (this.lines.length <= 1) {
			sb.append(line.line + "\n" + line.pointerStr());
		} else {
			sb.append("> File '" + this.filename + "', line " + line.lineNumber + ", col " + (line.index+1) + ":\n");
			
			int slen = ("" + line.lineNumber+1).length();
			String formatStr = "%-" + slen + "." + slen + "s| ";
	
			if (line.lineIndex > 0) {
				sb.append(String.format(formatStr, line.lineNumber - 1));
				sb.append(this.lines[line.lineIndex-1] + "\n");
			}
			
			int colLen = String.format(formatStr, 0).length();
			String spacer = "";
			for (int i = 0; i < colLen; i++) spacer += " ";
			sb.append(String.format(formatStr, line.lineNumber));
			sb.append(line.line);
			sb.append("\n");
			sb.append(spacer + line.pointerStr() + "\n");
			
	
			if (line.lineIndex+1 < this.lines.length) {
				sb.append(String.format(formatStr, line.lineNumber + 1));
				sb.append(this.lines[line.lineIndex+1] + "\n");
			}
		}
		
		
		return sb.toString();
	}
	
	public SourceStringRef ref(int index) {
		return new SourceStringRef(this, index);
	}

	
	@Override
	public String toString() {
		return "StringSource(" + filename + ")";
	}
	
	public static void main(String[] args) {
		SourceString ss = new SourceString("\n\n\n\n\n\n\n\n1 :x;\n{y,\n  y y +\n}:double;\nx double :P", "main");
		System.out.println(ss);
		System.out.println(ss.getRawString());
		
		for (int i = 0; i < ss.length(); i++) {
			System.out.println(i + ": " + ss.getIndexedLine(i));
		}
		
		System.out.println(ss.getContextStr(16));
		
		System.out.println((new SourceString("{x, x x *}:square;", "main").getContextStr(3)));
		System.out.println((new SourceString("[1 2]\n{x, x x *}:square;", "main").getContextStr(3)));
		System.out.println((new SourceString("[1 2]\n{x, x x *}:square;", "main").getContextStr(10)));

		System.out.println((new SourceString("", "main").getContextStr(0)));


		
	}
}
