package aya.util.stringsearch;

public enum SearchMode {
	Exact(ExactMatcher::new),
	Regex(RegexMatcher::new),
	/**
	 * This matches by splitting the needle into a minimal set of parts that match the haystack sequentially.
	 * For example:
	 * <pre>
	 * needle = "foba"
	 * haystack = [
	 *   "FooBar"
	 *    ^^ ^^      1 split
	 *   "FizzOrBar"
	 *    ^   ^ ^^   2 splits
	 *   "FizzOrFoBar"
	 *          ^^^^ 0 splits
	 * ]
	 * </pre>
	 */
	Fuzzy(FuzzyMatcher::new);

	public final StringMatcherFactory matcherFactory;

	SearchMode(StringMatcherFactory matcherFactory) {
		this.matcherFactory = matcherFactory;
	}
}
