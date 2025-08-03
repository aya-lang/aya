package ui;


import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import aya.parser.CharacterParser;

@SuppressWarnings("serial")
public class CodeTextPane extends JTextPane {
	private boolean inFocus = false;
	
	final UndoManager undo = new UndoManager();
	 
	
	public CodeTextPane() {
		
		//Generate Tabset
		TabStop[] tabs = new TabStop[100];
		for (int i = 0; i < 100; i++) {
		    tabs[i] = new TabStop((i+1)*20, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
		}
		TabSet tabset = new TabSet(tabs);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
		StyleConstants.TabSet, tabset);
		setParagraphAttributes(aset, false);
		
		
		//Font
		getStyledDocument().putProperty(PlainDocument.tabSizeAttribute, 2);
		setFont(StyleTheme.DEFAULT.getFont());
		
		
		//Default Colors
		setForeground(StyleTheme.DEFAULT.getFgColor());
		setBackground(StyleTheme.DEFAULT.getBgColor());
		setCaretColor(StyleTheme.DEFAULT.getCaratColor()); 
		
		
		//Border
		setBorder(BorderFactory.createEmptyBorder());
		
		// Tab listener (autocomplete)
		this.addKeyListener(new KeyListener() {
            @Override 
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
            	case KeyEvent.VK_TAB:
					tabPressed();
					break;
                }
            }

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        });
		
		
		//Add Focus Listeners
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				inFocus = true;
				
			}
			public void focusLost(FocusEvent arg0) {
				inFocus = false;
			}
        });
		
		
		//Override the tab key so that a tab character is not created
	    InputMap inputMap = getInputMap();
	    KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
	    inputMap.put(enterStroke, enterStroke.toString());
	    
	    //Undo Feature
	    Document doc = this.getDocument();
	    
	    // Listen for undo and redo events
	    doc.addUndoableEditListener(new UndoableEditListener() {
	        public void undoableEditHappened(UndoableEditEvent evt) {
	            undo.addEdit(evt.getEdit());
	        }
	    });

	    // Create an undo action and add it to the text component
	    this.getActionMap().put("Undo",
	        new AbstractAction("Undo") {
	            public void actionPerformed(ActionEvent evt) {
	                try {
	                    if (undo.canUndo()) {
	                        undo.undo();
	                    }
	                } catch (CannotUndoException e) {
	                }
	            }
	       });

	    // Bind the undo action to ctl-Z
	    this.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

	    // Create a redo action and add it to the text component
	    this.getActionMap().put("Redo",
	        new AbstractAction("Redo") {
	            public void actionPerformed(ActionEvent evt) {
	                try {
	                    if (undo.canRedo()) {
	                        undo.redo();
	                    }
	                } catch (CannotRedoException e) {
	                }
	            }
	        });

	    // Bind the redo action to ctl-Y
	    this.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
		
		setEditable(true);
	}
	
	/** Inserts certain text at the carat */
	public void insertAtCaret(String str) {
		try {
			getDocument().insertString(getCaretPosition(), str, null);
		} catch (BadLocationException e) {
			//Do Nothing
		}
	}

	/** If the pane is in focus, attempt to replace a special character */
	public void tabPressed() {
		if (inFocus) {
			int carat = getCaretPosition();
			int offset = Math.max((carat - 14), 0);
			String s = null;
			try {
				s = getDocument().getText(offset, carat-offset);
				getDocument().remove(offset, carat-offset);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			s = CharacterParser.convertCharTabPress(s);
			insertAtCaret(s);
		}
	}
	
	/**Returns true if the TextPane is in focus */
	public boolean inFocus() {
		return inFocus;
	}
	
}
