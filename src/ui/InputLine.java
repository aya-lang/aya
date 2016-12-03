package ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.InputMap;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class InputLine extends CodeTextPane {
	
	private ArrayList<String> lastText = new ArrayList<String>();	
	private int lastTextID = 0;
	
	public InputLine() {
		setFont(StyleTheme.MONO_14);
		setForeground(StyleTheme.DEFAULT.getFgColor());
		setBackground(StyleTheme.ACCENT_COLOR);
		setCaretColor(StyleTheme.DEFAULT.getCaratColor());
		
		//Override the enter key so that a newline character is not created
	    InputMap inputMap = getInputMap();
	    KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	    inputMap.put(enterStroke, enterStroke.toString());
	}
	
	public void loadLastText() {
		if(lastText.size() == 0)
			return;
		
		lastTextID = lastTextID <= 0 ? 0 : lastTextID-1;	
		setText(lastText.get(lastTextID));
	}
	
	public void loadPrevText() {
		if(lastText.size() == 0)
			return;
		lastTextID = lastTextID >= lastText.size()-1 ? lastText.size()-1 : lastTextID+1;
		setText(lastText.get(lastTextID));
	}

	public void clear() {
		String text = getText();
		
		//Only add the text to the history if it is not the most recent entry
		//i.e. don't add multiple copies of the same text in a row
		if (lastText.size() > 0) {
				if (!lastText.get(lastText.size()-1).equals(text)) {
					lastText.add(text);
				}
		} else {
			lastText.add(text);
		}
		lastTextID = lastText.size();
		setText("");
	}
	
	public void makeEmpty() {
		setText("");
	}
}
