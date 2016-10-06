package ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import element.util.StringSearch;


@SuppressWarnings("serial")
public class QuickSearch extends JPanel {

	private int maxItemsToDisplay = 35;
	
	private static final int WIDTH = 400;
	
	private static final CompoundBorder BORDER = new CompoundBorder(
			BorderFactory.createMatteBorder(2,0,2,4,StyleTheme.DEFAULT.getBgColor()), new EmptyBorder(3, 5, 3, 5));
	
	
	
	private JScrollPane scrollResults;
	private CodeTextPane searchBar = new CodeTextPane();
	private JPanel results = new JPanel();
	private StringSearch strings;
	private GridBagConstraints gbc = new GridBagConstraints();
	
	public static JFrame activeFrame;
	public static QuickSearch activeQuickSearch;
	public static void newQSFrame(String[] data) {
		activeFrame = new JFrame("Quick Search");
		activeQuickSearch = new QuickSearch(data);
		activeFrame.add(activeQuickSearch);
		activeFrame.pack();
		activeFrame.setVisible(true);
		activeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		activeFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
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

	
	public QuickSearch(String[] stringList) {
		this.strings = new StringSearch(stringList);
		
		//Size
		setMaximumSize(new Dimension(WIDTH, 500));
		setMinimumSize(new Dimension(WIDTH, 500));
		setPreferredSize(new Dimension(WIDTH, 500));
		
		//Border
		setBorder(new CompoundBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.ACCENT_COLOR), BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.DEFAULT.getBgColor())));

		
		//Layout
		setLayout(new BorderLayout());
		
		//KeyListeners
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyChar()==KeyEvent.VK_TAB) {
					searchBar.tabPressed();
				}
			}
			public void keyReleased(KeyEvent arg0) {
				if(arg0.isControlDown())
					return;
				strings.appendToFilter(searchBar.getText());
				refreshList();
				scrollToTop();
			}
			public void keyTyped(KeyEvent arg0) {}
		};
		
		//Search Bar
		searchBar.addKeyListener(keyListener);
		searchBar.setPreferredSize(new Dimension(10, 20));
		add(searchBar, BorderLayout.NORTH);
		
		//Results & Layout
		results.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx=1.0;
		results = new JPanel();
		results.setLayout(new GridBagLayout());
		
		
		
		//Wrap the results pane so the results stay at the top
		JPanel resultsWrapper = new JPanel();
		resultsWrapper.setBackground(StyleTheme.DEFAULT.getBgColor());
		resultsWrapper.setLayout(new BorderLayout());
		resultsWrapper.add(results, BorderLayout.NORTH);

		//Wrap the results in a scroll pane
		scrollResults = new JScrollPane(resultsWrapper);
		scrollResults.setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.setBorder(BorderFactory.createEmptyBorder());
		scrollResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollResults.getVerticalScrollBar().setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		add(scrollResults);
		
		//Put items in list
		refreshList();
		scrollToTop();
		
	}
	
	public static void updateHelpTextInFrame(String[] strs) {
		activeQuickSearch.updateHelpText(strs);
	}
	
	public void updateHelpText(String[] strings) {
		String currentSearch = this.strings.getFilter();
		this.strings = new StringSearch(strings);
		this.strings.applyNewFilter(currentSearch);
		refreshList();
	}
	
	/** Sets the maximum number of items to display in the list 
	 * (default is 25) */
	public void setMaxItemsToDisplay(int max) {
		maxItemsToDisplay = max;
	}
	
	/** Sets the current list */
	public void setList(String[] list) {
		this.strings = new StringSearch(list);
		refreshList();
	}
	
	/** Runs the search bars as a filter on the strings and then updates
	 * the newly added strings
	 */
	public void refreshList() {
		results.removeAll();
		gbc.gridx = 0;
		gbc.gridy = 0;

		int itemsToDisplay = strings.getFilteredItems().size();
		if(itemsToDisplay > maxItemsToDisplay) {
			itemsToDisplay = maxItemsToDisplay;
		}
		
		
		for(int i = 0; i < itemsToDisplay; i++) {
			gbc.gridy++;
			results.add(new StringDisplay(strings.getFilteredItems().get(i)), gbc);
		}
		results.validate();
		results.repaint();
		scrollResults.validate();
		scrollResults.repaint();
	}
	
	/** Scrolls to the top of the list */
	public void scrollToTop() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		   public void run() { 
		       scrollResults.getVerticalScrollBar().setValue(0);
		   }
		});
	}
	
	@Override
	public void grabFocus() {
		searchBar.grabFocus();
	}
	
	public class StringDisplay extends JTextArea {
		
		StringDisplay(String str) {
			super(0,0);
			this.setText(str);
			this.setWrapStyleWord(true);
			this.setLineWrap(true);
			this.setFont(StyleTheme.MONO_11);
			this.setBackground(StyleTheme.ACCENT_COLOR);
			this.setForeground(StyleTheme.DEFAULT.getFgColor());
			this.setBorder(QuickSearch.BORDER);
			this.setEditable(false);
			
		}
			
	}
}
