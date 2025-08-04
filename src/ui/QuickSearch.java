package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.LayeredHighlighter.LayerPainter;

import aya.StaticData;
import aya.util.StringSearch;
import aya.util.stringsearch.MatchInfo;
import aya.util.stringsearch.MatchPosition;
import aya.util.stringsearch.SearchMode;
import ui.settings.QuickSearchSettings;
import ui.settings.SettingsManager;


public class QuickSearch extends JPanel {
	// Pro: this prevents the slow Swing rendering from blocking input, making the UI feel less laggy while typing.
	// Con: UI might feel less responsive on fast machines.
	private static final int reRenderTimeoutMillis = 150;

	private static final CompoundBorder BORDER = new CompoundBorder(
			BorderFactory.createMatteBorder(2, 0, 2, 4, StyleTheme.DEFAULT.getBgColor()),
			new EmptyBorder(3, 5, 3, 5)
	);
	private static final CompoundBorder BORDER_HIGHLIGHT = new CompoundBorder(
			BorderFactory.createMatteBorder(2, 0, 2, 4, getSettings().getHighlightColor()),
			new EmptyBorder(3, 5, 3, 5)
	);

	public static JFrame activeFrame;
	public static QuickSearch activeQuickSearch;

	public static void newQSFrame(String[] data) {
		activeFrame = new JFrame("Quick Search");
		activeQuickSearch = new QuickSearch(data);
		activeFrame.add(activeQuickSearch);
		activeFrame.pack();
		activeFrame.setVisible(true);
		activeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		activeFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				activeFrame.setVisible(false);
			}
		});
	}

	public static boolean isFrameActive() {
		return activeFrame != null;
	}

	public static void frameFocus() {
		activeFrame.setVisible(true);
		activeQuickSearch.grabFocus();
	}

	private static QuickSearchSettings getSettings() {
		return SettingsManager.getUiSettings().getQuickSearch();
	}

	private static StringSearch initStringSearch(String[] strings) {
		StringSearch result = new StringSearch(strings);
		result.setSearchMode(getSettings().getSearchMode());
		result.setCaseSensitive(getSettings().isCaseSensitive());
		return result;
	}

	private final JScrollPane scrollResults;
	private final CodeTextPane searchBar = new CodeTextPane();
	private final JPanel results = new JPanel();
	private final JSplitPane resultSplitPane;
	private final StringDisplay detailsPanel;
	private final JScrollPane detailScroll;

	private int maxItemsToDisplay = Integer.MAX_VALUE;
	private StringSearch strings;
	private Timer reRenderTimer;
	private int currentSelection;

	public QuickSearch(String[] stringList) {
		this.strings = initStringSearch(stringList);

		//Border
		setBorder(new CompoundBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.ACCENT_COLOR), BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.DEFAULT.getBgColor())));

		//Layout
		setLayout(new BorderLayout());

		//KeyListeners
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				switch (arg0.getKeyCode()) {
					case KeyEvent.VK_TAB:
						searchBar.tabPressed();
						break;
					case KeyEvent.VK_DOWN:
						changeSelection(1);
						break;
					case KeyEvent.VK_UP:
						changeSelection(-1);
						break;
					default:
						return;
				}
				arg0.consume();
			}

			public void keyReleased(KeyEvent arg0) {
				if (arg0.isControlDown())
					return;
				switch (arg0.getKeyCode()) {
					case KeyEvent.VK_TAB:
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_UP:
						arg0.consume();
						break;
					default:
						startReRenderTimer();
						break;
				}
			}

			public void keyTyped(KeyEvent arg0) {
			}
		};

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.setBackground(StyleTheme.DEFAULT.getBgColor());
		add(topPanel, BorderLayout.NORTH);
		Dimension topPadding = new Dimension(6, 0);

		// TopPanel -> Search Bar
		searchBar.addKeyListener(keyListener);
		searchBar.setPreferredSize(new Dimension(10, 20));
		topPanel.add(searchBar, BorderLayout.NORTH);

		// TopPanel -> Cc Checkbox
		JCheckBox ccCheckbox = new JCheckBox("Cc", getSettings().isCaseSensitive());
		ccCheckbox.setBackground(StyleTheme.DEFAULT.getBgColor());
		ccCheckbox.setForeground(StyleTheme.DEFAULT.getFgColor());
		topPanel.add(Box.createRigidArea(topPadding));
		topPanel.add(ccCheckbox);
		ccCheckbox.addChangeListener(e -> {
			boolean caseSensitive = ccCheckbox.isSelected();
			strings.setCaseSensitive(caseSensitive);
			getSettings().setCaseSensitive(caseSensitive);
			SettingsManager.scheduleSaveUiSettings();
			startReRenderTimer();
		});

		// TopPanel -> searchMode combobox
		JComboBox<SearchMode> searchModeCombo = new JComboBox<>(SearchMode.values());
		searchModeCombo.setSelectedItem(getSettings().getSearchMode());
		searchModeCombo.setForeground(StyleTheme.DEFAULT.getFgColor());
		searchModeCombo.setBackground(StyleTheme.DEFAULT.getBgColor());
		topPanel.add(Box.createRigidArea(topPadding));
		topPanel.add(searchModeCombo);
		searchModeCombo.addActionListener(e -> {
			SearchMode newSearchMode = (SearchMode) searchModeCombo.getSelectedItem();
			strings.setSearchMode(newSearchMode);
			getSettings().setSearchMode(newSearchMode);
			SettingsManager.scheduleSaveUiSettings();
			startReRenderTimer();
		});

		// TopPanel -> extra options
		JButton extraOptsButton = new JButton("...");
		extraOptsButton.setForeground(StyleTheme.DEFAULT.getFgColor());
		extraOptsButton.setBackground(StyleTheme.DEFAULT.getBgColor());
		extraOptsButton.setPreferredSize(new Dimension(20, 20));
		JPopupMenu extraOptsMenu = new JPopupMenu();
		extraOptsMenu.setBackground(StyleTheme.DEFAULT.getBgColor());
		extraOptsMenu.setBorder(new EmptyBorder(6, 8, 6, 8));
		topPanel.add(Box.createRigidArea(topPadding));
		topPanel.add(extraOptsButton);
		extraOptsButton.addActionListener(e -> {
			// first, open the popup without showing it to determine the size
			extraOptsMenu.show(extraOptsButton, 0, 0);
			extraOptsMenu.setVisible(false);
			// then position it correctly
			javax.swing.SwingUtilities.invokeLater(() -> {
				extraOptsMenu.show(extraOptsButton, extraOptsButton.getWidth() - extraOptsMenu.getWidth(), extraOptsButton.getHeight());
				extraOptsMenu.setVisible(true);
			});
		});

		JPanel extraOptsMenuFlow = new JPanel();
		extraOptsMenuFlow.setLayout(new BoxLayout(extraOptsMenuFlow, BoxLayout.Y_AXIS));
		extraOptsMenuFlow.setBackground(StyleTheme.DEFAULT.getBgColor());
		extraOptsMenu.add(extraOptsMenuFlow);

		// TopPanel -> extra options -> number-input 'Summary Lines'
		JPanel summaryLinesOptRow = new JPanel();
		summaryLinesOptRow.setLayout(new BoxLayout(summaryLinesOptRow, BoxLayout.X_AXIS));
		summaryLinesOptRow.setBackground(StyleTheme.DEFAULT.getBgColor());
		JLabel summaryLinesLabel = new JLabel("Summary Lines");
		summaryLinesLabel.setForeground(StyleTheme.DEFAULT.getFgColor());
		summaryLinesLabel.setBackground(StyleTheme.DEFAULT.getBgColor());
		getSettings().setSummaryLines(Math.max(0, Math.min(99, getSettings().getSummaryLines())));
		JSpinner summaryLinesSpinner = new JSpinner(new SpinnerNumberModel(getSettings().getSummaryLines(), 0, 99, 1));
		summaryLinesSpinner.addChangeListener(e -> {
			int newNumSummaryLines = (int) summaryLinesSpinner.getValue();
			getSettings().setSummaryLines(newNumSummaryLines);
			SettingsManager.scheduleSaveUiSettings();
			startReRenderTimer();
		});
		summaryLinesOptRow.add(summaryLinesLabel);
		summaryLinesOptRow.add(Box.createRigidArea(topPadding));
		summaryLinesOptRow.add(summaryLinesSpinner);
		extraOptsMenuFlow.add(summaryLinesOptRow);
		summaryLinesOptRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

		// TopPanel -> extra options -> checkbox 'Show Details Panel'
		JCheckBox bShowDetailsCheckbox = new JCheckBox("Show Details Panel", getSettings().isShowDetailsPanel());
		bShowDetailsCheckbox.setForeground(StyleTheme.DEFAULT.getFgColor());
		bShowDetailsCheckbox.setBackground(StyleTheme.DEFAULT.getBgColor());
		bShowDetailsCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);
		bShowDetailsCheckbox.addChangeListener(e -> {
			boolean newShowDetails = bShowDetailsCheckbox.isSelected();
			getSettings().setShowDetailsPanel(newShowDetails);
			SettingsManager.scheduleSaveUiSettings();
			onShowDetailsChanged(newShowDetails);
		});
		extraOptsMenuFlow.add(bShowDetailsCheckbox);
		bShowDetailsCheckbox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		// Main Content Split
		resultSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		resultSplitPane.setResizeWeight(0.5);

		// Main Content Split -> Summary -> Result Items
		results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));

		// Wrap the results pane so the results stay at the top
		JPanel resultsWrapper = new JPanel();
		resultsWrapper.setBackground(StyleTheme.DEFAULT.getBgColor());
		resultsWrapper.setLayout(new BorderLayout());
		resultsWrapper.add(results, BorderLayout.NORTH);

		// Main Content Split -> Summary
		// Wrap the results in a scroll pane
		scrollResults = new JScrollPane(resultsWrapper);
		scrollResults.setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.setBorder(BorderFactory.createEmptyBorder());
		scrollResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollResults.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollResults.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		Dimension summarySize = new Dimension(getSettings().getSummaryPanelWidth(), getSettings().getPanelHeight());
		scrollResults.setPreferredSize(summarySize);
		scrollResults.getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				QuickSearchSettings settings = getSettings();
				settings.setSummaryPanelWidth(scrollResults.getWidth());
				settings.setPanelHeight(scrollResults.getHeight());
				scrollResults.setPreferredSize(new Dimension(settings.getDetailPanelWidth(), settings.getPanelHeight()));
				SettingsManager.scheduleSaveUiSettings();

				Component viewPort = e.getComponent();
				int width = viewPort.getWidth();
				for (Component component : results.getComponents()) {
					component.setSize(width, component.getHeight());
				}
			}
		});
		Insets textInsets = BORDER.getBorderInsets(null);
		int lineHeight = this.getFontMetrics(StyleTheme.MONO_11).getHeight();
		scrollResults.getVerticalScrollBar().setUnitIncrement(lineHeight + textInsets.top + textInsets.bottom);

		// Main Content Split -> Details
		detailsPanel = new StringDisplay();
		detailScroll = new JScrollPane(detailsPanel);
		detailScroll.setBackground(StyleTheme.DEFAULT.getBgColor());
		detailScroll.setBorder(BorderFactory.createEmptyBorder());
		detailScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		detailScroll.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		Dimension detailSize = new Dimension(getSettings().getDetailPanelWidth(), getSettings().getPanelHeight());
		detailScroll.setPreferredSize(detailSize);
		detailScroll.getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				QuickSearchSettings settings = getSettings();
				settings.setDetailPanelWidth(detailScroll.getWidth());
				settings.setPanelHeight(detailScroll.getHeight());
				detailScroll.setPreferredSize(new Dimension(settings.getDetailPanelWidth(), settings.getPanelHeight()));
				SettingsManager.scheduleSaveUiSettings();
			}
		});

		if (getSettings().isShowDetailsPanel()) {
			resultSplitPane.add(scrollResults);
			resultSplitPane.add(detailScroll);
			add(resultSplitPane);
		} else {
			add(scrollResults);
		}

		doReRender();
	}

	public static void updateHelpTextInFrame(String[] strs) {
		activeQuickSearch.updateHelpText(strs);
	}

	public void updateHelpText(String[] strings) {
		this.strings = initStringSearch(strings);
		doReRender();
	}

	/**
	 * Sets the maximum number of items to display in the list
	 * (default is unlimited)
	 */
	public void setMaxItemsToDisplay(int max) {
		maxItemsToDisplay = max;
	}

	private void onShowDetailsChanged(boolean newShowDetails) {
		if (newShowDetails) {
			remove(scrollResults);
			resultSplitPane.setLeftComponent(scrollResults);
			scrollResults.setPreferredSize(new Dimension(getSettings().getSummaryPanelWidth(), scrollResults.getPreferredSize().height));
			detailsPanel.setPreferredSize(new Dimension(getSettings().getDetailPanelWidth(), detailsPanel.getPreferredSize().height));
			add(resultSplitPane);
		} else {
			remove(resultSplitPane);
			scrollResults.setPreferredSize(new Dimension(getSettings().getSummaryPanelWidth(), scrollResults.getPreferredSize().height));
			add(scrollResults);
		}
		validate();
		activeFrame.pack();
		repaint();
	}

	private void setCurrentSelection(int newSelection) {
		Component[] resultItems = results.getComponents();
		if (currentSelection >= 0 && currentSelection < resultItems.length)
			((JComponent) resultItems[currentSelection]).setBorder(BORDER);
		if (newSelection >= 0 && newSelection < resultItems.length)
			((JComponent) resultItems[newSelection]).setBorder(BORDER_HIGHLIGHT);

		this.currentSelection = newSelection;
		scrollToSelection();
		List<MatchInfo> matches = strings.getMatches();
		if (newSelection >= 0 && newSelection < matches.size()) {
			MatchInfo match = matches.get(newSelection);
			String fullText = strings.getItem(match);
			detailsPanel.updateContent(fullText, match);
			javax.swing.SwingUtilities.invokeLater(() -> detailScroll.getVerticalScrollBar().setValue(0));
		} else {
			detailsPanel.updateContent("", null);
		}
	}

	/**
	 * Sets the current list
	 */
	public void setList(String[] list) {
		this.strings = initStringSearch(list);
		doReRender();
	}

	private void startReRenderTimer() {
		if (reRenderTimer != null) {
			reRenderTimer.cancel();
		}
		reRenderTimer = new Timer();
		reRenderTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				doReRender();
			}
		}, reRenderTimeoutMillis);
	}

	private void doReRender() {
		// remember the item that was referenced previously
		Integer previousSelectionRawIdx = strings.getMatches().stream().skip(currentSelection).map(x -> x.index).findFirst().orElse(null);
		strings.applyFilter(searchBar.getText());
		strings.sortFilterResults();
		refreshList();

		// Find the previously selected item in the new filtered list
		if (previousSelectionRawIdx != null) {
			setCurrentSelection(
					IntStream.range(0, strings.getMatches().size())
							.filter(i -> strings.getMatches().get(i).index == previousSelectionRawIdx)
							.findFirst().orElse(0)
			);
		} else {
			setCurrentSelection(0);
		}
	}

	private void changeSelection(int delta) {
		int newSelection = Math.max(0, Math.min(strings.getMatches().size() - 1, currentSelection + delta));
		if (newSelection == currentSelection)
			return;
		setCurrentSelection(newSelection);
	}

	/**
	 * Runs the search bars as a filter on the strings and then updates
	 * the newly added strings
	 */
	public void refreshList() {
		results.removeAll();
		IntStream.range(0, strings.getMatches().size())
				.limit(maxItemsToDisplay)
				.forEach(i -> {
					MatchInfo match = strings.getMatches().get(i);
					String summaryText = strings.getItem(match).lines()
							.limit(getSettings().getSummaryLines() == 0 ? Integer.MAX_VALUE : getSettings().getSummaryLines())
							.collect(Collectors.joining(System.lineSeparator()));
					StringDisplay strDisplay = new StringDisplay(i, summaryText, match, i == currentSelection);
					results.add(strDisplay);
					strDisplay.addMouseListener(strDisplay);
				});
		scrollResults.validate();
		scrollResults.repaint();
	}

	private void scrollToSelection() {
		if (currentSelection == 0) {
			javax.swing.SwingUtilities.invokeLater(() -> scrollResults.getVerticalScrollBar().setValue(0));
		} else {
			javax.swing.SwingUtilities.invokeLater(() -> results.scrollRectToVisible(results.getComponent(currentSelection).getBounds()));
		}
	}

	@Override
	public void grabFocus() {
		searchBar.grabFocus();
	}

	private static LayerPainter searchHighlightPainter;

	private static LayerPainter getSearchHighlightPainter() {
		if (searchHighlightPainter == null) {
			searchHighlightPainter = new DefaultHighlightPainter(getSettings().getHighlightColor());
		}
		return searchHighlightPainter;
	}

	public class StringDisplay extends JTextArea implements MouseListener {
		private final int itemIdx;

		StringDisplay() {
			this(-1, "", null, false);
		}

		StringDisplay(int itemIdx, String str, MatchInfo matchInfo, boolean highlight) {
			super(0, 0);
			this.itemIdx = itemIdx;
			this.setWrapStyleWord(true);
			this.setLineWrap(true);
			this.setFont(StyleTheme.MONO_11);
			this.setBackground(StyleTheme.ACCENT_COLOR);
			this.setForeground(StyleTheme.DEFAULT.getFgColor());
			this.setBorder(highlight ? QuickSearch.BORDER_HIGHLIGHT : QuickSearch.BORDER);
			this.setEditable(false);
			updateContent(str, matchInfo);
		}

		public void updateContent(String text, MatchInfo matchInfo) {
			this.setText(text);
			if (matchInfo != null && matchInfo.matchPositions != null) {
				for (MatchPosition matchPosition : matchInfo.matchPositions) {
					try {
						// surprisingly, this works for the summary fields without any extra bounds checks
						this.getHighlighter().addHighlight(matchPosition.offset, matchPosition.offset + matchPosition.length, getSearchHighlightPainter());
					} catch (BadLocationException e) {
						try (PrintStream err = StaticData.IO.err()) {
							err.println("StringSearch yielded an invalid match position (" + matchPosition + ")" + e.getMessage());
							e.printStackTrace(err);
						}
					}
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			QuickSearch.this.setCurrentSelection(itemIdx);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
}
