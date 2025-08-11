package aya.util.stringsearch;

import org.apache.commons.lang3.Strings;

public class ExactMatcher implements StringMatcher {
	private final boolean caseSensitive;
	private final String needle;

	public ExactMatcher(boolean caseSensitive, String needle) {
		this.caseSensitive = caseSensitive;
		this.needle = needle;
	}

	@Override
	public MatchPosition[] match(String haystackItem) {
		int matchOffset = caseSensitive
				? haystackItem.indexOf(needle)
				: Strings.CI.indexOf(haystackItem, needle);
		return matchOffset < 0
				? null
				: new MatchPosition[]{new MatchPosition(matchOffset, needle.length())};
	}
}
