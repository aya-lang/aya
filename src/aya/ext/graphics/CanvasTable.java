package aya.ext.graphics;

import java.util.HashMap;

public class CanvasTable {
	private int _canvas_count;
	private HashMap<Integer, Canvas> _canvas_table;
	
	public CanvasTable() {
		_canvas_table = new HashMap<>();
		_canvas_count = 0;
	}
	
	public int newCanvas(String name, int width, int height) {
		_canvas_count++;
		Canvas c = new Canvas(name, width, height);
		_canvas_table.put(_canvas_count, c);
		return _canvas_count;
	}
	
	/** returns null if canvas id does not exist */
	public Canvas getCanvas(int id) {
		return _canvas_table.get(id);
	}
	
	public void close(int id) {
		Canvas c = _canvas_table.get(id);
		if (c != null) {
			c.close();
		}		
	}
	
	public static void main(String[] args) {
		CanvasTable ci = new CanvasTable();
		
		int id1 = ci.newCanvas("c1", 200, 200);
		int id2 = ci.newCanvas("c2", 200, 200);
		
		Canvas canvas1 = ci.getCanvas(id1);
		Canvas canvas2 = ci.getCanvas(id2);
		
		canvas1.show();
		canvas2.show();
		
		canvas1.close();
		canvas2.close();
	}
}
