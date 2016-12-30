package aya;

import java.util.LinkedList;
import java.util.Queue;

public class AyaIn {
	Queue<String> _inputQueue;
	
	public AyaIn() {
		_inputQueue = new LinkedList<String>();
	}
	
	public void add(String s) {
		_inputQueue.add(s);
	}
	
	public boolean hasInput() {
		return _inputQueue.size() != 0;
	}
	
	public String next() {
		return _inputQueue.poll();
	}
}
