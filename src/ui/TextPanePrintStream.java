package ui;

import ui.components.FixedTextPane;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class TextPanePrintStream extends OutputStream {

	private JTextPane _textPane;
	private StyledDocument _doc;
	MutableAttributeSet _col;
	
	public TextPanePrintStream() {
		_textPane = new FixedTextPane();
		_textPane.setEditable(false);
		_doc = _textPane.getStyledDocument();
		_textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
		_textPane.setForeground(StyleTheme.DEFAULT.getFgColor());
		_textPane.setBackground(StyleTheme.DEFAULT.getBgColor());
		_textPane.setCaretColor(StyleTheme.DEFAULT.getCaratColor());

		setColor(Color.WHITE);
	}
	
	private final int BUFFER_SIZE = 4096;
	byte[] buffer = new byte[BUFFER_SIZE];
	int pos = 0;
	
	@Override
	public void write(int b) throws IOException {
		buffer[pos++] = (byte)b;
		if (pos >= BUFFER_SIZE) {
			flush();
		}
	}
	
	@Override
	public void flush() throws IOException {
		byte[] str_bytes = new byte[pos];
		
		System.arraycopy(buffer, 0, str_bytes, 0, pos);
		String out = new String(str_bytes, "UTF-8");
		//System.out.println(out);
		print(out);
		
		pos = 0;
		
        // scrolls the text area to the end of data
		goToEnd();
	}
//	
	/**
	 * Prints the string to the console in the given color
	 * @param string
	 * @param color
	 */
	public void print(String string) {
		//Print the text
		try {
			if (string.equals("\b")) {
				if (_doc.getLength() > 0) {
                    _doc.remove(_doc.getLength() - 1, 1);
                }
			} else {
				_doc.insertString(_doc.getLength(), string, _col);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void setColor(Color color) {
		String colorname = ""+color.getRed()+"_"+color.getGreen()+"_"+color.getBlue()+"";
		
		//First check to see if we have already added this color
		MutableAttributeSet col = _textPane.getStyle(colorname);
		if (col == null) {
			//If we haven't, add it
			col = _textPane.addStyle(colorname, null);
			StyleConstants.setForeground(col, color);
		}
	}
	
	public JTextPane getTextPane() {
		return _textPane;
	}
	

	public void goToEnd() {
		_textPane.setCaretPosition(_textPane.getDocument().getLength());		
	}
	
	public void clear() {
		_textPane.setText("");
	}


}
