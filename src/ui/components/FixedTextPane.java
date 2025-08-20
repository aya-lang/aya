package ui.components;

import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Rectangle;

/**
 * Mirrors the reasonable overrides of {@link javax.swing.JTextArea} that were forgotten in all other Text Components...
 */
@SuppressWarnings("serial")
public class FixedTextPane extends JTextPane {
	private int rowHeight = 0;
	private int columnWidth = 0;

	private int getRowHeight() {
		if (rowHeight == 0) {
			rowHeight = getFontMetrics(getFont()).getHeight();
		}
		return rowHeight;
	}

	private int getColumnWidth() {
		if (columnWidth == 0) {
			columnWidth = getFontMetrics(getFont()).charWidth('m');
		}
		return columnWidth;
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		rowHeight = 0;
		columnWidth = 0;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return orientation == SwingConstants.HORIZONTAL ? getColumnWidth() : getRowHeight();
	}
}
