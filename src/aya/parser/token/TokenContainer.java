package aya.parser.token;

import java.util.ArrayList;

import aya.parser.tokens.Token;

public abstract class TokenContainer {
	protected ArrayList<Token> tokens;
	
	protected TokenContainer(ArrayList<Token> ts) {
		this.tokens = ts;
	}
	
	/** Returns the size of the inner token container */
	public int size() {
		return tokens.size();
	}
	
	/** Returns the arrayList container */
	public ArrayList<Token> getArrayList() {
		return tokens;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Token t : tokens) {
			sb.append(t.toString()).append(" ");
		}
		return "< " + sb.toString() + ">";
	}
}
