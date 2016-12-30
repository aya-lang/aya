package ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import aya.Aya;


@SuppressWarnings("serial")
public class EditorWindow extends JPanel {
	
	private static final int WIDTH = 400;
	
	private static final CompoundBorder BORDER = new CompoundBorder(
				BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.ACCENT_COLOR),
				BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.DEFAULT.getBgColor()));
	
	
	private JScrollPane scrollResults;
	private CodeTextPane editor = new CodeTextPane();
	
	public static JFrame activeFrame;
	public static EditorWindow activeEditor;
	private JMenu menu;
	private JMenuBar menuBar;
	
	public static void newEditorFrame(AyaIDE ide) {
		activeFrame = new JFrame("Editor");
		activeEditor = new EditorWindow();
		activeFrame.add(activeEditor);
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
		activeEditor.grabFocus();
	}

	
	public EditorWindow() {			
		//Size
		setMaximumSize(new Dimension(WIDTH, 500));
		setMinimumSize(new Dimension(WIDTH, 500));
		setPreferredSize(new Dimension(WIDTH, 500));
		
		//Border
		setBorder(BORDER);

		
		//Layout
		setLayout(new BorderLayout());

			
		
		//Menu Bar
		menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(100, 20));

			
		//Tools
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		//Insert Filename
		JMenuItem mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				insertFilenameAtCarat();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Insert Filename..");
		menu.add(mi);
		
		//Run
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Run");
		menu.add(mi);
		menuBar.add(menu);
				
		
		
		//Help
		//Quick Search
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(QuickSearch.isFrameActive()) {
					QuickSearch.frameFocus();
				} else {
					QuickSearch.newQSFrame(Aya.getQuickSearchData());
				}
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Quick Search");
		menu.add(mi);
		
		//Key Bindings
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(activeFrame, ""
						+ "ctrl+Q		  Quick Search\n"
						+ "ctrl+I		  Interpreter\n"
						+ "ctrl+E         Editor\n"
						);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Key Bindings");
		menu.add(mi);
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(activeFrame, "Aya IDE\nNicholas Paul\nVersion: " + AyaIDE.VERSION_NAME + "\nAya Version: " + Aya.VERSION_NAME);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("About");
		menu.add(mi);
		menuBar.add(menu);
		
		
		activeFrame.setJMenuBar(menuBar);
		
		//Editor
		editor.setPreferredSize(new Dimension(WIDTH-10, 400));
		add(editor, BorderLayout.NORTH);

		//Wrap the results in a scroll pane
		scrollResults = new JScrollPane(editor);
		scrollResults.setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.setBorder(BorderFactory.createEmptyBorder());
		scrollResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollResults.getVerticalScrollBar().setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		add(scrollResults);
		
	}
	
	
	/** Scrolls to the top of the list */
	public void scrollToTop() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		   public void run() { 
		       scrollResults.getVerticalScrollBar().setValue(0);
		   }
		});
	}
	
	public void insertFilenameAtCarat() {
		File file = AyaIDE.chooseFile();
		if(file != null) {
			String path = file.getPath();
			path = path.replace("\\", "\\\\");
            editor.insertAtCaret(path);
		}
	}
	
	public static boolean hasText() {
		return activeEditor != null ? !activeEditor.editor.getText().equals("") : false;
	}
	
	public void run() {
		String txt = editor.getText();
		Aya.getInstance().queueInput(txt);
	}
	
	@Override
	public void grabFocus() {
		editor.grabFocus();
	}
}
