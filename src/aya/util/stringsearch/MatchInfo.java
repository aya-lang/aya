package aya.util.stringsearch;

public class MatchInfo {
	/**
	 * The index into the haystack. (points at the matched item)
	 */
	// Note: I am preferring the 'index' over the haystack string here, because this makes some optimizations easier.
	// For example: creating a list of GUI Items for the haystack and toggling their visibility based on matches.
	public final int index;
	/**
	 * The substrings of the haystack-item that caused the match.
	 */
	public final MatchPosition[] matchPositions;

	public MatchInfo(int index, MatchPosition... matchPositions) {
		this.index = index;
		this.matchPositions = matchPositions;
	}
}
