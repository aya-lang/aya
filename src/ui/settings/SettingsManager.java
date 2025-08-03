package ui.settings;

import aya.AyaPrefs;
import aya.StaticData;
import aya.util.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;

public class SettingsManager {
	private static final String uiSettingsFileName = "ui-settings.json";

	/**
	 * The File in which ui-settings are stored. Do not access directly, use {@link #getSettingsFile()} instead.
	 */
	private static File _uiSettingsFile;
	private static UiSettings _uiSettings;

	private static File getSettingsFile() {
		if (_uiSettingsFile == null) {
			String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
			if (StringUtils.isNotBlank(xdgConfigHome)) {
				File settingsDirectory = new File(xdgConfigHome, "aya");
				if (!settingsDirectory.isDirectory() && !settingsDirectory.mkdir()) {
					throw new RuntimeException("The settings directory does not exist and could not be created. (" + settingsDirectory.getAbsolutePath() + ")");
				}
				_uiSettingsFile = new File(settingsDirectory, uiSettingsFileName);
			} else {
				_uiSettingsFile = new File(AyaPrefs.getAyaRootDirectory(), uiSettingsFileName);
			}
		}
		return _uiSettingsFile;
	}

	public static UiSettings getUiSettings() {
		if (_uiSettings == null) {
			_uiSettings = loadUiSettings();
		}
		return _uiSettings;
	}

	public static void saveUiSettings() {
		File settingsFile = getSettingsFile();
		JSONObject settingsObj = getUiSettings().storeToJson();
		String settingsStr = settingsObj.toString(4);
		try {
			StaticData.FILESYSTEM.write(settingsFile, settingsStr.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Unable to store settings to file. (" + settingsFile.getAbsolutePath() + ")", e);
		}
	}

	private static UiSettings loadUiSettings() {
		File settingsFile = getSettingsFile();
		if (!settingsFile.isFile()) {
			// no settings saved yet, load defaults
			return new UiSettings();
		}

		try {
			JSONObject settingsObj = new JSONObject(FileUtils.readAllText(settingsFile));
			int storedVersion = settingsObj.getInt("version");
			if (storedVersion < UiSettings.latestVersion) {
				// apply migrations to the settingsObj as needed
			}
			UiSettings result = new UiSettings();
			result.loadFromJson(settingsObj);
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Unable to read settings file. (" + settingsFile.getAbsolutePath() + ")", e);
		}
	}
}
