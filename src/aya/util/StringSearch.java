package aya.util;

import aya.util.stringsearch.MatchInfo;
import aya.util.stringsearch.MatchPosition;
import aya.util.stringsearch.SearchMode;
import aya.util.stringsearch.StringMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An easily filter-able collection of strings
 *
 * @author Nick
 */
public class StringSearch {
	private final List<String> allItems;
	private List<MatchInfo> matches;
	private String filter;
	private SearchMode searchMode = SearchMode.Exact;
	private boolean caseSensitive = true;

	public StringSearch(String[] items) {
		allItems = new ArrayList<>(Arrays.asList(items));
		clearFilter();
	}

	public StringSearch(List<String> items) {
		allItems = new ArrayList<>(items);
		clearFilter();
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Applies a new filter to the string list
	 */
	public void applyFilter(String filter) {
		// I'm leaving a potential optimization on the table here:
		// If the previous mode and current mode are 'Exact', and caseSensitive has not changed, and oldFilter is a substring of newFilter.
		// Then you can reuse the previous search result.
		// The extra code for that does not seem worth the performance gain.

		StringMatcher matcher = searchMode.matcherFactory.createMatcher(caseSensitive, filter);
		matches = new ArrayList<>();
		for (int i = 0; i < allItems.size(); i++) {
			MatchPosition[] matchPositions = matcher.match(allItems.get(i));
			if (matchPositions != null)
				matches.add(new MatchInfo(i, matchPositions));
		}
		this.filter = filter;
	}

	/**
	 * Sorts the current filtered items based on their match strength.
	 */
	public void sortFilterResults() {
		switch (searchMode) {
			case Exact:
				break; // all matches are equal, do not sort.
			case Regex:
				// matches might differ by length. Prioritize longer matches.
				matches.sort((a, b) -> Integer.compare(b.matchPositions[0].length, a.matchPositions[0].length));
				break;
			case Fuzzy:
				// matches might differ by number of splits. Prioritize fewer splits.
				matches.sort(Comparator.comparingInt(a -> a.matchPositions.length));
				break;
		}
	}

	/**
	 * Returns the items in the filtered list
	 */
	public List<String> getFilteredItems() {
		if (filter == null) {
			return allItems;
		}
		return matches.stream().map(match -> allItems.get(match.index)).collect(Collectors.toList());
	}

	public String getItem(MatchInfo match) {
		return allItems.get(match.index);
	}

	public List<MatchInfo> getMatches() {
		return matches;
	}

	/**
	 * Returns the current filter
	 */
	public String getFilter() {
		return this.filter;
	}

	/**
	 * Returns all of the items (regardless of the filter)
	 */
	public String[] getAllItems() {
		return this.allItems.toArray(new String[allItems.size()]);
	}

	/**
	 * Adds s to the list of items. Does not update the filtered items
	 */
	public void add(String s) {
		allItems.add(s);
	}

	/**
	 * Adds s to the list if the list does not already contain s. Does not update filtered items
	 */
	public void addUnique(String s) {
		if (!allItems.contains(s)) {
			allItems.add(s);
		}
	}

	/**
	 * Clears the current filter
	 */
	public void clearFilter() {
		this.filter = null;
		this.matches = IntStream.range(0, allItems.size())
				.mapToObj(MatchInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return getFilteredItems().toString();
	}

	/**
	 * Apply a search and return an arraylist but do not modify the object in any way
	 */
	public List<String> staticSearch(String str) {
		StringMatcher matcher = SearchMode.Exact.matcherFactory.createMatcher(true, str);
		return allItems.stream().filter(item -> matcher.match(item) != null).collect(Collectors.toList());
	}

}
