package element.infix;

import java.util.ArrayList;

import element.exceptions.EndOfInputError;

/**
 * Class supports a stack style data structure using pop()
 * and a queue style structure using next. Both should not be used
 * at the same tome
 * @author Nick
 *
 */
public class InfixItemList {
	ArrayList<InfixItem> tokens;
	int front; //From front
	
	/** Default constructor */
	public InfixItemList() {
		tokens = new ArrayList<InfixItem>();
		front = 0;
	}
	
	/** Initialized constructor */
	public InfixItemList(ArrayList<InfixItem> tokens) {
		this.tokens = tokens;
		front = 0;
	}
	
	/** Copy constructor */
	@SuppressWarnings("unchecked")
	public InfixItemList(InfixItemList tl) {
		this.tokens = (ArrayList<InfixItem>)tl.tokens.clone();
		this.front = tl.front;
	}
	
	/** resets the counter variables */
	public void init() {
		this.front = 0;
		
	}
	
	/** Returns the number of elements in the TokenList (Remember, next() and pop() do NOT
	 * change the size of the actual token list. Use hasNext() or popHasNext()) */
	public int size() {
		return tokens.size();
	}
	
	/** "Removes" and returns the first token in the string */
	public InfixItem next() throws EndOfInputError {
		if(front >= tokens.size()) {
			throw new EndOfInputError("Unexpected End of Input");
		}
		InfixItem t = tokens.get(front);
		front++;
		return t;
	}
	
	
	/** Returns the next token without removing it */
	public InfixItem peekNext() {
		return tokens.get(front);
	}
	
	/** Returns the (next token + i) without removing it [lookAhead(0) == peek()]
	 * returns null if element does not exist there (index out of bounds) */
	public InfixItem lookAhead(int i) {
		if ((front + i) >= tokens.size()) {
			return null;
		}
		return tokens.get(front+i);
	}
	
	/** Returns false if there is no more data to be parsed (opposite of isEmpty)*/
	public boolean hasNext() {
		return front < tokens.size();
	}
	
	public InfixItem pop() {
		return tokens.remove(tokens.size()-1);
	}
	
	/** Appends a token to the end of the list */
	public void add(InfixItem t) {
		tokens.add(t);
	}
	
	/** Returns the inner ArrayList container of the TokenList */
	public ArrayList<InfixItem> getArrayList() {
		return tokens;
	}
	
	/** Returns the ith element of the token array */
	public InfixItem get(int i) {
		return tokens.get(i);
	}
		
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(InfixItem t:tokens) {
			sb.append(t.toString() + ' ');
		}
		return sb.toString().substring(0, sb.length()-1);
	}

	public void addAll(ArrayList<? extends InfixItem> items) {
		tokens.addAll(items);
	}
}
 