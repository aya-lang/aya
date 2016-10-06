package ui;

import java.awt.Color;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


@SuppressWarnings("serial")
public class ConsoleWindow extends JScrollPane {
	
	private static JTextPane textpane = new JTextPane();
	
	private static boolean allowDebugPrinting = false;
	private StyledDocument doc = textpane.getStyledDocument();
	
	
	public ConsoleWindow() {
	    super(textpane);
	    textpane.setEditable(false);
		doc = textpane.getStyledDocument();
		textpane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        textpane.setForeground(StyleTheme.DEFAULT.getFgColor());
        textpane.setBackground(StyleTheme.DEFAULT.getBgColor());
        textpane.setCaretColor(StyleTheme.DEFAULT.getCaratColor());

        
        //No border
        setBorder(BorderFactory.createEmptyBorder());
        
        //Always scroll bar
		//setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


	}
	

	
	/**
	 * Prints the string to the console in the given color
	 * @param string
	 * @param color
	 */
	public void print(String string, Color color) {
		String colorname = ""+color.getRed()+"_"+color.getGreen()+"_"+color.getBlue()+"";
		
		//First check to see if we have already added this color
		MutableAttributeSet col = textpane.getStyle(colorname);
		if (col == null) {
			//If we haven't, add it
			col = textpane.addStyle(colorname, null);
			StyleConstants.setForeground(col, color);
		}
		
		//Print the text
		try {
			doc.insertString(doc.getLength(), string, col);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Prints normal black text to the console
	 * @param text
	 */
	public void print(String text) {
		print(text, StyleTheme.DEFAULT.getFgColor());
	}
		
	public void println(String text) {
		print(text + "\n");
	}

	public void printEx(String string) {
		print(string, Color.red);
	}
	
	public void printExComplete(String message, Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		if(message == null) {
			printEx(sw.toString());
		} else {
			printEx(message + "\n" + sw.toString());
		}
	}
	
	public void printQuiet(String s) {
		print(s, StyleTheme.MONOKAI_COMMENT);		
	}	
	
	public void printWarn(String s) {
		print(s + "\n", Color.YELLOW);
	}
	
	public void printQ(String s) {
		print(s + "\n", Color.GRAY);
	}
	
	public void printDebug(String s) {
		if(allowDebugPrinting)
			print(s + "\n", Color.GRAY);
	}

	
	public void allowDebug(boolean b) {
		print("Debug printing set to: " + b + "\n", Color.GRAY);
		allowDebugPrinting = b;
	}
	
	public boolean getAllowDebugPrinting() {
		return allowDebugPrinting;
	}

	public static void goToEnd() {
		textpane.setCaretPosition(textpane.getDocument().getLength());		
	}
	
	public void clear() {
		textpane.setText("");
	}

	public void printEx(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		//out.setError(true);
		printEx(sw.toString());		
	}
}
