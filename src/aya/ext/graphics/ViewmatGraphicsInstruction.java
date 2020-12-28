package aya.ext.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.util.Casting;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;

public class ViewmatGraphicsInstruction extends NamedInstruction {

	private CanvasInterface _canvas_interface;
	private Color[] _colors;
	
	public ViewmatGraphicsInstruction(CanvasInterface canvas_interface) {
		super("graphics.viewmat");
		_doc = "LN draw a matrix on the canvas";
		_canvas_interface = canvas_interface;
		
		// Init Colors
		_colors = new Color[256];
		for (int i = 0; i <= 255; i++) {
			_colors[i] = new Color(i, i, i);
		}
	}
	

	@Override
	public void execute(Block block) {
		final Obj o_id = block.pop();
		final Obj o_data = block.pop();
		
		NumberList data;
		int width;
		int canvas_id;
		try {
			canvas_id = ((aya.obj.number.Number)o_id).toInt();
			List data_list = Casting.asList(o_data);
			width = Casting.asList(data_list.getExact(0)).length();
			data = data_list.flatten().toNumberList();
		} catch (ClassCastException e) {
			throw new TypeError(this, "LN", o_id, o_data);
		}
	
		Canvas cvs = _canvas_interface.getCanvas(canvas_id);
		drawMat(data, width, cvs);

	}

	private void drawMat(NumberList data, int width, Canvas canvas) {
		int height = data.length() / width;
		Graphics2D g = canvas.getG2D();
		Number max = data.max();
		Number min = data.min();
		// norm= (xi - min(x)) / (max(x) - min(x))
		NumberList norm = data.sub(min).div(NumberMath.sub(max, min));
		// Convert to bytes
		int[] int_data = norm.mul(Num.fromInt(255)).toIntArray();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int idx = x + y * width;
				g.setColor(_colors[int_data[idx]]);
				g.fillRect(x, y, 1, 1);
			}
		}
	}

}



