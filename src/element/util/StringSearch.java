package element.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A easily filter-able collection of strings
 * @author Nick
 *
 */
public class StringSearch {
	ArrayList<String> allItems;
	ArrayList<String> filteredItems;
	String filter;
	
	public StringSearch(String[] items) {
		allItems = new ArrayList<String>(Arrays.asList(items));
		filteredItems = allItems;
	}
	
	public StringSearch(ArrayList<String> items) {
		allItems = new ArrayList<String>(items);
		filteredItems = allItems;
	}
	
	/** Applies a new filter to the string list */
	public ArrayList<String> applyNewFilter(String filter) {
		this.filter = filter;
		runFilter();
		return filteredItems;
	}
	
	/** Compares the current filter and s and adds the new content to the filter.
	 * If the characters are different before the end of the current filter is reached,
	 * create a brand new filter */
	public ArrayList<String> appendToFilter(String strIn) {
		if(filter == null) {
			return applyNewFilter(strIn);
		}
		
		//If the new string is shorter than the current filter, create a new filter
		if(filter.length() <= strIn.length()) {
			return applyNewFilter(strIn);
		}
		
		//Check if the first few chars of the existing filter matches strIn
		if (strIn.startsWith(filter)) {
			return addToFilter(strIn.substring(filter.length(), strIn.length()));
		}
		return applyNewFilter(strIn);
	}
	
	/** Appends a string to the current filter. Faster than creating a new filter
	 * because it only searches through the previously filtered items.
	 */
	public ArrayList<String> addToFilter(String str) {
		if(filter == null) {
			filter = str;
		} else {
			this.filter = this.filter + str;
		}
		
		ArrayList<String> newFilteredItems = new ArrayList<String>();
		//Only search the already filtered items
		for (String item : filteredItems) {
			if(item.contains(filter)) {
				newFilteredItems.add(item);
			}
		}
		filteredItems = newFilteredItems;
		return filteredItems;
	}
	
	/** Returns the items in the filtered list */
	public ArrayList<String> getFilteredItems() {
		if (filter == null) {
			return allItems;
		}
		return filteredItems;
	}
	
	/** Returns the filtered items as an array */
	public String[] getFilteredItemsAsArray() {
		if (filter == null) {
			return allItems.toArray(new String[allItems.size()]);
		}
		return filteredItems.toArray(new String[filteredItems.size()]);
	}
	
	/** Runs the current filter on all the items */
	public void runFilter() {
		this.filteredItems = new ArrayList<String>();
		for (String item : allItems) {
			if(item.contains(filter)) {
				filteredItems.add(item);
			}
		}
	}
	
	/** Returns the current filter */
	public String getFilter() {
		return this.filter;
	}
	
	/** Returns all of the items (regardless of the filter)  */
	public String[] getAllItems() {
		return this.allItems.toArray(new String[allItems.size()]);
	}
	
	/** Adds s to the list of items. Does not update the filtered items */
	public void add(String s) {
		allItems.add(s);
	}
	
	/** Adds s to the list if the list does not already contain s. Does not update filtered items */
	public void addUnique(String s) {
		if(!allItems.contains(s)) {
			allItems.add(s);
		}
	}
	
	/** Adds the list to the list of items. Does not
	 * update the filtered items */
	public void addAll(Collection<? extends String> list) {
		allItems.addAll(list);
	}
	
	/** Adds the list to the list of items. Does not
	 * update the filtered items */
	public void addAll(String[] list) {
		allItems.addAll(new ArrayList<String>(Arrays.asList(list)));
	}
	
	/** Clears the current filter */
	public void clearFilter() {
		this.filter = null;
		this.filteredItems = allItems;
	}
	
	@Override
	public String toString() {
		if(filter != null)
			return filteredItems.toString();
		return allItems.toString();
	}
}
