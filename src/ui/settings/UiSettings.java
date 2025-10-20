package ui.settings;

import org.json.JSONObject;

/**
 * The primary settings Object that contains/references all settings.
 */
public class UiSettings implements ISettings {
	public static final int latestVersion = 0;

	/**
	 * A monotonically increasing number used to apply automatic migrations of old settings-files.
	 */
	private int version = latestVersion;

	private QuickSearchSettings quickSearch = new QuickSearchSettings();

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public QuickSearchSettings getQuickSearch() {
		return quickSearch;
	}

	public void setQuickSearch(QuickSearchSettings quickSearch) {
		this.quickSearch = quickSearch;
	}

	@Override
	public void loadFromJson(JSONObject storedSettings) {
		version = storedSettings.getInt("version");
		quickSearch.loadFromJson(storedSettings.getJSONObject("quickSearch"));
	}

	@Override
	public JSONObject storeToJson() {
		JSONObject result = new JSONObject();
		result.put("version", version);
		result.put("quickSearch", quickSearch.storeToJson());
		return result;
	}
}
