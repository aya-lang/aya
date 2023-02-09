package aya.ext.graphics;

import aya.Aya;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.util.SizeBoundedQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CanvasCursorListener implements MouseListener, MouseMotionListener {

	private static Symbol X;
	private static Symbol Y;
	private static Symbol BUTTON;
	private static Symbol CLICKS;

	private final Set<Integer> pressedButtons = Collections.synchronizedSet(new HashSet<>());
	private final SizeBoundedQueue<ClickInfo> clickHistory = new SizeBoundedQueue<>(16);
	// swing seems to fire an event per single pixel movement, so the move-buffer should be a bit bigger
	private final SizeBoundedQueue<MoveInfo> moveHistory = new SizeBoundedQueue<>(128);

	public CanvasCursorListener() {
		SymbolTable symbols = Aya.getInstance().getSymbols();
		X = symbols.getSymbol("x");
		Y = symbols.getSymbol("y");
		BUTTON = symbols.getSymbol("button");
		CLICKS = symbols.getSymbol("clicks");
	}

	public List<Integer> getPressedButtons() {
		synchronized (pressedButtons) {
			return new ArrayList<>(pressedButtons);
		}
	}

	public List<ClickInfo> getClickHistory() {
		return clickHistory.dequeToList();
	}

	public List<MoveInfo> getMoveHistory() {
		return moveHistory.dequeToList();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		clickHistory.offer(new ClickInfo(e));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		pressedButtons.add(e.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressedButtons.remove(e.getButton());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		moveHistory.offer(new MoveInfo(e));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		moveHistory.offer(new MoveInfo(e));
	}

	public static class ClickInfo {
		public final int x;
		public final int y;
		public final int button;
		public final int numClicks;

		public ClickInfo(MouseEvent clickEvent) {
			x = clickEvent.getX();
			y = clickEvent.getY();
			button = clickEvent.getButton();
			numClicks = clickEvent.getClickCount();
		}

		public Dict toDict() {
			Dict result = new Dict();
			result.set(X, Num.fromInt(x));
			result.set(Y, Num.fromInt(y));
			result.set(BUTTON, Num.fromInt(button));
			result.set(CLICKS, Num.fromInt(numClicks));
			return result;
		}
	}

	public static class MoveInfo {
		public final int x;
		public final int y;

		public MoveInfo(MouseEvent moveEvent) {
			x = moveEvent.getX();
			y = moveEvent.getY();
		}

		public Dict toDict() {
			Dict result = new Dict();
			result.set(X, Num.fromInt(x));
			result.set(Y, Num.fromInt(y));
			return result;
		}
	}
}
