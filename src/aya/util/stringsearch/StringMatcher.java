package aya.util.stringsearch;

@FunctionalInterface
public interface StringMatcher {
	/**
	 * @param haystackItem the string to match against
	 * @return {@code null} if no match was found. Otherwise, a list of Positions that constitute the match.
	 */
	MatchPosition[] match(String haystackItem);
}
