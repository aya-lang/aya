package ui;
import java.awt.Color;
import java.awt.Font;


public class StyleTheme {
	
	//Fonts
	public static final Font MONO_11 = new Font(Font.MONOSPACED, Font.PLAIN, 11);
	public static final Font MONO_12 = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	public static final Font MONO_14 = new Font(Font.MONOSPACED, Font.PLAIN, 14);
	public static final Font MONO_16 = new Font(Font.MONOSPACED, Font.PLAIN, 16);
	
	//Font Colors
	public static final Color MONOKAI_BLUE = new Color(102, 217, 239);
	public static final Color MONOKAI_PINK = new Color(249, 38 , 114);
	public static final Color MONOKAI_YELLOW = new Color(230, 219, 116);
	public static final Color MONOKAI_COMMENT = new Color(117, 113, 94);
	public static final Color MONOKAI_WHITE = new Color(248, 248, 242);
	public static final Color MONOKAI_PURPLE = new Color(174, 129, 255);

	public static final Color SUBLIME_FG = new Color(248, 248, 242);
	public static final Color SUBLIME_BG = new Color(39 , 40 , 34);
	public static final Color OFF_WHITE_BG = new Color(234, 238, 238);
	public static final Color OFF_GREY_FG = new Color(56, 56, 56);
	public static final Color ACCENT_COLOR = new Color(72,72,68);

	public static final StyleTheme DEFAULT = new StyleTheme(MONO_16, SUBLIME_FG, SUBLIME_BG, SUBLIME_FG);
	

	
	private Font font;
	private Color fgColor;
	private Color bgColor;
	private Color caratColor;
	
	public StyleTheme(){};
	
	public StyleTheme(Font ft, Color fg, Color bg, Color ct) {
		this.font = ft;
		this.fgColor = fg;
		this.bgColor = bg;
		this.caratColor = ct;
	}
	
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public Color getFgColor() {
		return fgColor;
	}
	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
	}
	public Color getBgColor() {
		return bgColor;
	}
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}
	public Color getCaratColor() {
		return caratColor;
	}
	public void setCaratColor(Color caratColor) {
		this.caratColor = caratColor;
	}

	
}
