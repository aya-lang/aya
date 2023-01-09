package aya.ext.graphics;

import aya.util.SizeBoundedQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CanvasKeyListener implements KeyListener {

	private final Map<Integer, KeyEvent> pressedKeys = Collections.synchronizedMap(new HashMap<>());
	private final SizeBoundedQueue<Character> typedChars = new SizeBoundedQueue<>(16);

	public ArrayList<KeyEvent> getPressedKeys() {
		synchronized (pressedKeys) {
			return new ArrayList<>(pressedKeys.values());
		}
	}

	/**
	 * @return a list of characters, in order, that were typed since the last time this method was invoked.
	 */
	public ArrayList<Character> consumeTypedChars() {
		return typedChars.dequeToList();
	}

	private int getKeyIdentifier(KeyEvent key) {
		return (key.getKeyLocation() << (Integer.SIZE - 3)) | key.getKeyCode();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		typedChars.offer(e.getKeyChar());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		pressedKeys.put(getKeyIdentifier(e), e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressedKeys.remove(getKeyIdentifier(e));
	}
}
