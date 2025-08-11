package aya.util.stringsearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher implements StringMatcher {
	private final Pattern needle;

	private static Pattern compilePatternSafe(boolean caseSensitive, String pattern) {
		try {
			return caseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		} catch (Exception e) {
			pattern = Pattern.quote(pattern);
			return caseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		}
	}

	public RegexMatcher(boolean caseSensitive, String needle) {
		this.needle = compilePatternSafe(caseSensitive, needle);
	}

	@Override
	public MatchPosition[] match(String haystackItem) {
		Matcher matcher = needle.matcher(haystackItem);
		return matcher.find()
				? new MatchPosition[]{new MatchPosition(matcher.start(), matcher.end() - matcher.start())}
				: null;
	}
}
