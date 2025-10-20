package ui.settings;

import org.json.JSONObject;

public interface ISettings {
	void loadFromJson(JSONObject storedSettings);

	JSONObject storeToJson();
}
