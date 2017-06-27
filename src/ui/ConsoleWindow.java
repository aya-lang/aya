package ui;

import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;


@SuppressWarnings("serial")
public class ConsoleWindow extends JScrollPane {
	
	private static TextPanePrintStream textpane = new TextPanePrintStream();
		
	public ConsoleWindow() {
	    super(textpane.getTextPane());
        
        //No border
        setBorder(BorderFactory.createEmptyBorder());
        
        //Always scroll bar
		//setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	}

	public void goToEnd() {
		textpane.goToEnd();
	}
	
	public void clear() {
		textpane.clear();
	}
	
	public TextPanePrintStream getPrintStream() {
		return textpane;
	}
}
