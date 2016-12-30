package aya.parser.tokens;

import java.util.ArrayList;

import aya.parser.token.TokenQueue;

public abstract class CollectionToken extends StdToken{
	protected ArrayList<Token> col;
	
	public CollectionToken(int type, String data, ArrayList<Token> col) {
		super(data, type);
		this.col = col;
	}
	
	/** Splits a list of tokens wherever a comma is */
	protected static ArrayList<TokenQueue> splitCommas(ArrayList<Token> tokens) {
		ArrayList<TokenQueue> out = new ArrayList<TokenQueue>();
		int splits = 0;
		out.add(new TokenQueue()); 		//Instantiate the first list
		for(Token t : tokens) {
			if (t.isa(Token.COMMA)) {
				splits++;
				out.add(new TokenQueue());
			} else {
				out.get(splits).add(t);
			}
		}
		return out;
	}
}
