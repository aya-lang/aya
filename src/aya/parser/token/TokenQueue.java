package aya.parser.token;

import java.util.ArrayList;

import aya.exceptions.parser.EndOfInputError;
import aya.parser.tokens.Token;

public class TokenQueue  {
	protected ArrayList<Token> queue;
	
	public TokenQueue() {
		queue = new ArrayList<Token>();
	}
	
	@SuppressWarnings("unchecked")
	public TokenQueue(TokenQueue other) {
		this.queue = (ArrayList<Token>) other.queue.clone();
	}
	
	public TokenQueue(ArrayList<Token> tokens) {
		this.queue = tokens;
	}

	public void add(Token t) {
		queue.add(t);
	}
	
	public void replaceNext(Token t) {
		queue.set(0, t);
	}
	
	/** Removes and returns the next token in the queue */
	public Token next() throws EndOfInputError {
		Token tmp = queue.get(0);
		queue.remove(0);
		return tmp;
	}
	
	/** Removes and retuens the last token in the queue */
	public Token popBack() {
		return queue.remove(queue.size()-1);	
	}

	/** Returns the next token without removing it */
	public Token peek() {
		return queue.get(0);
	}
	
	/** Returns the (next token + i) without removing it [lookAhead(0) == peek()]
	 * returns null if element does not exist there (index out of bounds) */
	public Token lookAhead(int i) {
		return queue.get(i);
	}
	
	/** Returns false if there is no more data to be parsed (opposite of isEmpty)*/
	public boolean hasNext() {
		return queue.size() > 0;
	}
	
	public ArrayList<Token> getArrayList() {
		return queue;
	}

	public int size() {
		return queue.size();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("< ");
		for (Token t : queue) {
			sb.append(t.toString()).append(" ");
		}
		return sb.append(">").toString();
	}

	

}
