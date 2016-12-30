package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import aya.AyaPrefs;
import aya.Aya;
import aya.InteractiveAya;
import aya.OutputString;

@SuppressWarnings("serial")
public class MyConsole extends JPanel {
	
	private Aya _aya;
	
	ConsoleWindow cw = new ConsoleWindow();
	public static InputLine il = new InputLine();
	
	private int width = 500;
	private int height = 250;
	
	public MyConsole(Aya aya) {
		this._aya = aya;
		init();
	}


	public void init() {
		this.setLayout(new BorderLayout(0,0));
		
		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(10,10));
		
		cw.setPreferredSize(new Dimension(width, height));
		il.setPreferredSize(new Dimension(width, 20));
		il.setMaximumSize(new Dimension(width, 0));
		cw.setMaximumSize(new Dimension(width, 0));
		
		cw.setMinimumSize(new Dimension(10,10));
		il.setMinimumSize(new Dimension(10,10));
	
		
		this.add(cw, BorderLayout.CENTER);
		this.add(il, BorderLayout.SOUTH);
		
		setBorder(new CompoundBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.ACCENT_COLOR), BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.DEFAULT.getBgColor())));
	}
	
	
	public InputLine getInputLine() {
		return il;
	}
	
	public void eval(String s) {
		eval(s,s);
	}
	
	public void eval(String s, String input_name) {
		cw.println(AyaPrefs.getPrompt() + input_name);
		
		int status = Aya.RETURN_ERROR;
		
		try {
			status = InteractiveAya.processInput(_aya, s);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			_aya.printEx(sw.toString());
		}
		
		switch (status) {
		case Aya.RETURN_SUCCESS:
			if(!_aya.getOut().isEmpty()){
				ArrayList<OutputString> output = _aya.getOut().dump();
				
				for(OutputString str : output) {
					switch(str.getType()) {
					case OutputString.PRINT:
						cw.print(str.toString());
						break;
					case OutputString.NORMAL:
						cw.print(str.toString());
						break;
					case OutputString.ERROR:
						cw.printEx(str.toString());
						cw.println("");
						break;
					case OutputString.WARN:
						cw.printWarn(str.toString());
						cw.println("");
						break;
					case OutputString.QUIET:
						cw.printQuiet(str.toString());
						cw.println("");
						break;
					case OutputString.COLOR:
						cw.print(str.toString(), str.getColor());
						break;
					default:
						cw.printEx("Set up OutoutString type in MyConsole.eval()");	
					}
				}
				cw.println("\n"); //Two lines
			}
			break;
		case Aya.CLEAR_CONSOLE:
			cw.clear();
			break;
		case Aya.RETURN_EXIT:
			AyaIDE.exit();
			break;
		case Aya.RETURN_ERROR:
			cw.printEx(_aya.getOut().dumpAsString());
			break;
		default:
			throw new RuntimeException("Implement status in MyConsole.eval()");
		
		}
		
		ConsoleWindow.goToEnd();
	}
	
	public void clrAndFocus() {
		il.clear();
		il.grabFocus();
	}
	
	public ConsoleWindow out() {
		return this.cw;
	}
}
