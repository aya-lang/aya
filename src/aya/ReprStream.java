package aya;

import java.util.ArrayList;
import java.util.Stack;

import aya.obj.Obj;

public class ReprStream {
	
	static class Line {
		int indent;
		StringBuilder content;
		public Line(int indent) {
			this.indent = indent;
			this.content = new StringBuilder();
		}

		public void delTrailingSpaces() {
			content = new StringBuilder(removeTrailingSpaces(content.toString()));
		}
	}
	
	ArrayList<Line> _lines;
	int _current_indent;
	String _indent_str;
	Line _current_line;
	Stack<Obj> _visited;
	
	public ReprStream() {
		_lines = new ArrayList<ReprStream.Line>();
		_current_indent = 0;
		_indent_str = "  ";
		_visited = new Stack<Obj>();
		_current_line = new Line(_current_indent);
	}
	
	public void println() {
		println("");
	}
	
	public void println(String str) {
		String[] ss = str.split("\n");
		for (String s : ss) {
			printlnInternal(s);
		}
	}
	
	public void print(String str) {
		boolean trailing_newline = false;
		if (str.length() > 0) {
			trailing_newline = str.charAt(str.length()-1) == '\n';
		}
		String[] ss = str.split("\n");
		for (int i = 0; i < ss.length; i++) {
			if (i == ss.length - 1) {
				// If we are on the last line, only call println if there was
				// a trailing newline character
				if (trailing_newline) {
					printlnInternal(ss[i]);
				} else {
					printInternal(ss[i]);
				}
			} else {
				printlnInternal(ss[i]);
			}
		}
	}
	
	private void printlnInternal(String str) {
		printInternal(str);
		newLine();
	}
	
	private void printInternal(String str) {
		_current_line.content.append(str);
	}
	
	public void incIndent() {
		_current_indent += 1;
	}
	
	private void newLine() {
		_lines.add(_current_line);
		_current_line = new Line(_current_indent);
	}
	
	public void decIndent() {
		_current_indent -= 1;
		if (_current_indent < 0) throw new RuntimeException("ReprStream: Unbalanced indents!");
	}
	
	public void currentLineMatchIndent() {
		_current_line.indent = _current_indent;
	}
	
	public void delTrailingNewline() {
		if (_lines.size() > 0 && _current_line.content.length() == 0) {
			_current_line = _lines.remove(_lines.size()-1);
		}
	}
	
	/** Delete characters on current line only */
	public void backspace(int chars) {
		int new_len = _current_line.content.length() - chars;
		if  (new_len < 0) new_len = 0;
		_current_line.content.setLength(new_len);
	}
	
	private void appendLine(StringBuilder sb, Line line) {
		for (int i = 0; i < line.indent; i++) sb.append(_indent_str);
		sb.append(line.content);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Line line : _lines) {
			appendLine(sb, line);
			sb.append('\n');
		}
		appendLine(sb, _current_line);
		return sb.toString();
	}

	public String toStringOneline() {
		StringBuilder sb = new StringBuilder();
		for (Line line : _lines) {
			sb.append(line.content);
			sb.append(' ');
		}
		sb.append(_current_line.content);
		return sb.toString().trim();
	}
	
	private boolean visitedContainsExact(Obj o) {
		for (Obj x : _visited) {
			if (x == o) return true;
		}
		return false;
	}
	
	public boolean visit(Obj o) {
		// Is o already in our visited stack?
		if (visitedContainsExact(o)) {
			return false;
		} else {
			_visited.push(o);
			return true;
		}
	}
	
	public void popVisited(Obj o) {
		if (o != _visited.peek()) {
			throw new RuntimeException("popVisited: out of sync!");
		}
		_visited.pop();
	}

	public void print(int val) {
		print(String.valueOf(val));
	}


	
    public static String removeTrailingSpaces(String param) 
    {
        if (param == null)
            return null;
        int len = param.length();
        for (; len > 0; len--) {
            if (!Character.isWhitespace(param.charAt(len - 1)))
                break;
        }
        return param.substring(0, len);
    }

	public void delTrailingSpaces() {
		_current_line.delTrailingSpaces();
	}
}
