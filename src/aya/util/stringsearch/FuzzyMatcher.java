package aya.util.stringsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Related work:
 * <ul>
 *     <li><a href="https://doi.org/10.1007/978-3-540-30551-4_43">Minimum Common String Partition</a> is superficially similar, but searches for matching permutations of partitions of two strings.</li>
 *     <li><a href="https://doi.org/10.1137/0206024">Fast Pattern Matching in Strings</a> (the KMP algorithm)</li>
 * </ul>
 * <br/> Whereas this algorithm searches for matching subsequences of partitions.
 */
public class FuzzyMatcher implements StringMatcher {
	private final String needle;
	private final char[] needleChars;
	private final boolean caseSensitive;
	private final Map<Integer, int[]> kmpNextByNeedleOffset = new HashMap<>();

	public static void main(String[] args) {
		FuzzyMatcher matcher = new FuzzyMatcher(false, "abcab");
		for (MatchPosition position : matcher.match("ab_cab_abc_a_b")) {
			System.out.println("o=" + position.offset + " l=" + position.length);
		}
	}

	public FuzzyMatcher(boolean caseSensitive, String needle) {
		this.needle = caseSensitive ? needle : needle.toLowerCase(); // lazy hack :(
		this.needleChars = needle.toCharArray();
		this.caseSensitive = caseSensitive;
	}

	@Override
	public MatchPosition[] match(String haystackItem) {
		if (!caseSensitive)
			haystackItem = haystackItem.toLowerCase();

		Stack<MatchPosition> bestMatch = new Stack<>();
		int numGroups = findMinimumPartition(0, haystackItem, 0, Integer.MAX_VALUE, bestMatch);
		assert numGroups < 0 || numGroups == bestMatch.size();
		return numGroups < 0
				? null
				: bestMatch.toArray(new MatchPosition[numGroups]);
	}

	/**
	 * @return -1 if no matching partition was found. Otherwise, the number of groups needed to match the haystack.
	 */
	private int findMinimumPartition(int needleOffset, String haystackItem, int haystackOffset, int maxGroups, Stack<MatchPosition> resultContainer) {
		if (needleOffset >= needle.length()) {
			return 0; // needle matched completely
		}
		if (maxGroups <= 0) {
			return -1;
		}
		if (!canMatch(needleOffset, haystackItem, haystackOffset)) {
			return -1;
		}

		/* Greedily matching the longest prefix does not work.
		 *   For example: needle=abcab haystack=ab_cab_abc_a_b
		 *   BAD: [abc a b]
		 *   GOOD: [ab cab]
		 */

		List<Integer> matchingPrefixes = findAllMatchingPrefixes(needleOffset, haystackItem, haystackOffset);
		int prevMatchIdx = -1;
		int minSuffixGroups = Integer.MAX_VALUE;
		MatchPosition minSuffixMatch = null;
		for (int i = matchingPrefixes.size() - 1; i >= 0; i--) {
			int matchLen = i + 1;
			int matchIndex = matchingPrefixes.get(i);
			if (prevMatchIdx == matchIndex) {
				continue; // ignore this match, because the previous (longer) match at the same position will always give a better result
			}
			prevMatchIdx = matchIndex;

			int suffixGroups = findMinimumPartition(needleOffset + matchLen, haystackItem, matchIndex + matchLen, maxGroups - 1, resultContainer);
			if (suffixGroups < 0) {
				continue;
			}
			if (minSuffixMatch != null) {
				// remove the partial result that was pushed by the previous best match
				resultContainer.setSize(resultContainer.size() - minSuffixGroups);
			}

			minSuffixGroups = suffixGroups;
			minSuffixMatch = new MatchPosition(matchIndex, matchLen);
			maxGroups = minSuffixGroups; // restrict search such that only better solutions are explored
			if (suffixGroups <= 1)
				break; // best possible result, stop searching
		}

		if (minSuffixMatch != null) {
			resultContainer.add(0, minSuffixMatch);
			return minSuffixGroups + 1;
		}
		return -1;
	}

	private boolean canMatch(int needleOffset, String haystackItem, int haystackOffset) {
		int needleLenRemain = needle.length() - needleOffset;
		if ((haystackOffset + needleLenRemain) > haystackItem.length()) {
			return false; // remaining haystackItem is too short to match the remaining needle
		}

		char needleChar = needleChars[needleOffset];
		while (haystackOffset < haystackItem.length()) {
			if (needleChar == haystackItem.charAt(haystackOffset)) {
				needleOffset++;
				if (needle.length() == needleOffset)
					return true;
				needleChar = needleChars[needleOffset];
			}
			haystackOffset++;
		}
		return false;
	}

	private List<Integer> findAllMatchingPrefixes(int needleOffset, String haystackItem, int haystackOffset) {
		// This uses a minor variation of the KMP algorithm to efficiently find all prefixes.
		List<Integer> result = new ArrayList<>(needleChars.length);
		int len_needle = needleChars.length - needleOffset;
		assert len_needle > 0;

		int i_needle = 0;
		int len_hay = haystackItem.length();
		int[] kmp_next = getKmpNextTable(needleOffset);
		while (haystackOffset < len_hay) {
			char c_hay = haystackItem.charAt(haystackOffset);
			if (c_hay == needleChars[i_needle + needleOffset]) {
				if (result.size() <= i_needle) {
					// found a new longest prefix
					result.add(haystackOffset - i_needle);
					if ((i_needle + 1) == len_needle) {
						break; // matched entire needle
					}
				}
			} else {
				do {
					i_needle = kmp_next[i_needle];
				} while (i_needle >= 0 && c_hay != needleChars[i_needle]);
			}
			i_needle++;
			haystackOffset++;
		}
		return result;
	}

	private int[] getKmpNextTable(int needleOffset) {
		return kmpNextByNeedleOffset.computeIfAbsent(needleOffset, k -> {
			if (needle.isEmpty()) {
				return new int[0];
			} else {
				int[] kmp_next = new int[needleChars.length];
				kmp_next[0] = -1;
				int i_needle = 1;
				int i_check = 0;
				while (i_needle < needleChars.length) {
					if (needleChars[i_needle] == needleChars[i_check]) {
						kmp_next[i_needle] = kmp_next[i_check];
					} else {
						kmp_next[i_needle] = i_check;
					}
					i_needle++;
					i_check++;
				}
				return kmp_next;
			}
		});
	}
}
