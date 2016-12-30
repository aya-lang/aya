package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

@SuppressWarnings("serial")
public class MyConsole extends JPanel {
	
	private ConsoleWindow _out = new ConsoleWindow();
	private TextPaneInputStream _in = new TextPaneInputStream();
	
	private int width = 500;
	private int height = 250;
	
	public MyConsole() {
		init();
	}


	public void init() {
		this.setLayout(new BorderLayout(0,0));
		
		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(10,10));
		
		_out.setPreferredSize(new Dimension(width, height));
		_in.getInputLine().setPreferredSize(new Dimension(width, 20));
		_in.getInputLine().setMaximumSize(new Dimension(width, 0));
		_out.setMaximumSize(new Dimension(width, 0));
		
		_out.setMinimumSize(new Dimension(10,10));
		_in.getInputLine().setMinimumSize(new Dimension(10,10));
	
		
		this.add(_out, BorderLayout.CENTER);
		this.add(_in.getInputLine(), BorderLayout.SOUTH);
		
		setBorder(new CompoundBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.ACCENT_COLOR), BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.DEFAULT.getBgColor())));
	}
	
	
	public InputLine getInputLine() {
		return _in.getInputLine();
	}
	
	public void clrAndFocus() {
		_in.getInputLine().clear();
		_in.getInputLine().grabFocus();
	}
	
	public OutputStream getOut() {
		return _out.getPrintStream();
	}
	
	public InputStream getIn() {
		return _in;
	}


	public void clear() {
		_out.clear();
	}
}
