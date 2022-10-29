package aya.ext.graphics;

import aya.Aya;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CanvasCursorListener implements MouseListener, MouseMotionListener {

	private static final int maxEventHistory = 16;

	private static Symbol X;
	private static Symbol Y;
	private static Symbol BUTTON;
	private static Symbol CLICKS;
	private static Symbol TIME_MS;

	private final Queue<CursorInfo> clickHistory = new ArrayDeque<>(maxEventHistory);
	private final Queue<CursorInfo> dragHistory = new ArrayDeque<>(maxEventHistory);
	private final Queue<CursorInfo> moveHistory = new ArrayDeque<>(maxEventHistory);

	public CanvasCursorListener() {
		SymbolTable symbols = Aya.getInstance().getSymbols();
		X = symbols.getSymbol("x");
		Y = symbols.getSymbol("y");
		BUTTON = symbols.getSymbol("button");
		CLICKS = symbols.getSymbol("clicks");
		TIME_MS = symbols.getSymbol("time_ms");
	}

	public List<CursorInfo> getClickHistory() {
		return depleteHistoryToList(clickHistory);
	}

	public List<CursorInfo> getDragHistory() {
		return depleteHistoryToList(dragHistory);
	}

	public List<CursorInfo> getMoveHistory() {
		return depleteHistoryToList(moveHistory);
	}

	private List<CursorInfo> depleteHistoryToList(Queue<CursorInfo> queue) {
		ArrayList<CursorInfo> list = new ArrayList<>(queue);
		queue.clear();
		return list;
	}

	private synchronized void addEvent(Queue<CursorInfo> queue, MouseEvent e) {
		if (queue.size() >= maxEventHistory) {
			queue.remove();
		}
		queue.offer(new CursorInfo(e));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		addEvent(clickHistory, e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO does not track which button is pressed
		addEvent(dragHistory, e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		addEvent(moveHistory, e);
	}

	public static class CursorInfo {
		public final int x;
		public final int y;
		public final int button;
		public final int numClicks;
		public final long timestamp;

		public CursorInfo(MouseEvent clickEvent) {
			x = clickEvent.getX();
			y = clickEvent.getY();
			button = clickEvent.getButton();
			numClicks = clickEvent.getClickCount();
			timestamp = System.currentTimeMillis();
		}

		public Dict toDict() {
			Dict result = new Dict();
			result.set(X, Num.fromInt(x));
			result.set(Y, Num.fromInt(y));
			result.set(BUTTON, Num.fromInt(button));
			result.set(CLICKS, Num.fromInt(numClicks));
			result.set(TIME_MS, new Num(timestamp));
			return result;
		}
	}
}
