package aya.parser.token;

import java.util.Stack;

import aya.exceptions.parser.EndOfInputError;
import aya.parser.tokens.Token;

public class TokenStack {
	private Stack<Token> stack;
	
	public TokenStack() {
		this.stack = new Stack<Token>();
	}
	
	public TokenStack(TokenQueue other) {
		this.stack = new Stack<Token>();
		this.stack.addAll(other.queue);
	}
	
	/** Returns true if there exists a token that can be popped */
	public boolean hasNext() {
		return stack.size() != 0;
	}
	
	/** Removes and returns the last token in the stack */
	public Token pop() throws EndOfInputError {
		return stack.pop();
	}
	
	/** Returns the next token without removing it */
	public Token peek() {
		return stack.peek();
	}
	
	public void push(Token pt) {
		stack.push(pt);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("< ");
		for (Token t : stack) {
			sb.append(t.toString()).append(" ");
		}
		return sb.append(">").toString();
	}
	
	
}
