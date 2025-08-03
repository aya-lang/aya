package aya.util.stringsearch;

@FunctionalInterface
public interface StringMatcherFactory {
	/**
	 * @param needle the string to match with
	 */
	StringMatcher createMatcher(boolean caseSensitive, String needle);
}
