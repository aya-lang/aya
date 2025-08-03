package ui.settings;

import aya.util.ColorFactory;
import aya.util.stringsearch.SearchMode;
import org.json.JSONObject;

import java.awt.Color;

public class QuickSearchSettings implements ISettings {
	private SearchMode searchMode = SearchMode.Exact;
	private boolean caseSensitive = true;
	private Color highlightColor = new Color(255, 255, 0, 64);
	private int summaryPanelWidth = 200;
	private int detailPanelWidth = 400;
	private int panelHeight = 500;
	private int summaryLines = 1;
	private boolean showDetailsPanel = true;

	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	public int getSummaryPanelWidth() {
		return summaryPanelWidth;
	}

	public void setSummaryPanelWidth(int summaryPanelWidth) {
		this.summaryPanelWidth = summaryPanelWidth;
	}

	public int getDetailPanelWidth() {
		return detailPanelWidth;
	}

	public void setDetailPanelWidth(int detailPanelWidth) {
		this.detailPanelWidth = detailPanelWidth;
	}

	public int getPanelHeight() {
		return panelHeight;
	}

	public void setPanelHeight(int panelHeight) {
		this.panelHeight = panelHeight;
	}

	public int getSummaryLines() {
		return summaryLines;
	}

	public void setSummaryLines(int summaryLines) {
		this.summaryLines = summaryLines;
	}

	public boolean isShowDetailsPanel() {
		return showDetailsPanel;
	}

	public void setShowDetailsPanel(boolean showDetailsPanel) {
		this.showDetailsPanel = showDetailsPanel;
	}

	@Override
	public void loadFromJson(JSONObject storedSettings) {
		searchMode = storedSettings.optEnum(SearchMode.class, "searchMode", searchMode);
		caseSensitive = storedSettings.optBoolean("caseSensitive", caseSensitive);
		if (storedSettings.has("highlightColor")) {
			highlightColor = ColorFactory.web(storedSettings.getString("highlightColor"));
		}
		summaryPanelWidth = storedSettings.optInt("summaryPanelWidth", summaryPanelWidth);
		detailPanelWidth = storedSettings.optInt("detailPanelWidth", detailPanelWidth);
		panelHeight = storedSettings.optInt("panelHeight", panelHeight);
		summaryLines = storedSettings.optInt("summaryLines", summaryLines);
		showDetailsPanel = storedSettings.optBoolean("showDetailsPanel", showDetailsPanel);
	}

	@Override
	public JSONObject storeToJson() {
		JSONObject result = new JSONObject();
		result.put("searchMode", searchMode);
		result.put("caseSensitive", caseSensitive);
		result.put("highlightColor", ColorFactory.toWebString(highlightColor));
		result.put("summaryPanelWidth", summaryPanelWidth);
		result.put("detailPanelWidth", detailPanelWidth);
		result.put("panelHeight", panelHeight);
		result.put("summaryLines", summaryLines);
		result.put("showDetailsPanel", showDetailsPanel);
		return result;
	}
}
