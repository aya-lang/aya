package aya.ext.graphics;

import aya.ext.graphics.instruction.ArcGraphicsInstruction;
import aya.ext.graphics.instruction.ClearGraphicsInstruction;
import aya.ext.graphics.instruction.ClearRectGraphicsInstruction;
import aya.ext.graphics.instruction.CloseGraphicsInstruction;
import aya.ext.graphics.instruction.CopyRectGraphicsInstruction;
import aya.ext.graphics.instruction.EllipseGraphicsInstruction;
import aya.ext.graphics.instruction.IsOpenGraphicsInstruction;
import aya.ext.graphics.instruction.LineGraphicsInstruction;
import aya.ext.graphics.instruction.ListFontsGraphicsInstruction;
import aya.ext.graphics.instruction.NewGraphicsInstruction;
import aya.ext.graphics.instruction.OvalGraphicsInstruction;
import aya.ext.graphics.instruction.PathGraphicsInstruction;
import aya.ext.graphics.instruction.RectGraphicsInstruction;
import aya.ext.graphics.instruction.RotateGraphicsInstruction;
import aya.ext.graphics.instruction.RoundRectGraphicsInstruction;
import aya.ext.graphics.instruction.SaveGraphicsInstruction;
import aya.ext.graphics.instruction.ScaleGraphicsInstruction;
import aya.ext.graphics.instruction.SetAlphaGraphicsInstruction;
import aya.ext.graphics.instruction.SetBGColorGraphicsInstruction;
import aya.ext.graphics.instruction.SetColorAlphaGraphicsInstruction;
import aya.ext.graphics.instruction.SetColorGraphicsInstruction;
import aya.ext.graphics.instruction.SetFontGraphicsInstruction;
import aya.ext.graphics.instruction.SetPaintGradGraphicsInstruction;
import aya.ext.graphics.instruction.SetStrokeGraphicsInstruction;
import aya.ext.graphics.instruction.SetStrokeWidthGraphicsInstruction;
import aya.ext.graphics.instruction.ShearGraphicsInstruction;
import aya.ext.graphics.instruction.ShowGraphicsInstruction;
import aya.ext.graphics.instruction.TextGraphicsInstruction;
import aya.ext.graphics.instruction.TranslateGraphicsInstruction;
import aya.ext.graphics.instruction.ViewmatGraphicsInstruction;
import aya.instruction.named.NamedInstructionStore;

public class GraphicsInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		
		CanvasTable canvas_table = new CanvasTable();
		
		addInstruction(new ArcGraphicsInstruction(canvas_table));
		addInstruction(new ClearGraphicsInstruction(canvas_table));
		addInstruction(new ClearRectGraphicsInstruction(canvas_table));
		addInstruction(new CloseGraphicsInstruction(canvas_table));
		addInstruction(new CopyRectGraphicsInstruction(canvas_table));
		addInstruction(new EllipseGraphicsInstruction(canvas_table));
		addInstruction(new IsOpenGraphicsInstruction(canvas_table));
		addInstruction(new LineGraphicsInstruction(canvas_table));
		addInstruction(new ListFontsGraphicsInstruction(canvas_table));
		addInstruction(new NewGraphicsInstruction(canvas_table));
		addInstruction(new OvalGraphicsInstruction(canvas_table));
		addInstruction(new PathGraphicsInstruction(canvas_table));
		addInstruction(new RectGraphicsInstruction(canvas_table));
		addInstruction(new RotateGraphicsInstruction(canvas_table));
		addInstruction(new RoundRectGraphicsInstruction(canvas_table));
		addInstruction(new SaveGraphicsInstruction(canvas_table));
		addInstruction(new ScaleGraphicsInstruction(canvas_table));
		addInstruction(new SetAlphaGraphicsInstruction(canvas_table));
		addInstruction(new SetBGColorGraphicsInstruction(canvas_table));
		addInstruction(new SetColorGraphicsInstruction(canvas_table));
		addInstruction(new SetColorAlphaGraphicsInstruction(canvas_table));
		addInstruction(new SetFontGraphicsInstruction(canvas_table));
		addInstruction(new SetPaintGradGraphicsInstruction(canvas_table));
		addInstruction(new SetStrokeGraphicsInstruction(canvas_table));
		addInstruction(new SetStrokeWidthGraphicsInstruction(canvas_table));
		addInstruction(new ShearGraphicsInstruction(canvas_table));
		addInstruction(new ShowGraphicsInstruction(canvas_table));
		addInstruction(new TextGraphicsInstruction(canvas_table));
		addInstruction(new TranslateGraphicsInstruction(canvas_table));
		addInstruction(new ViewmatGraphicsInstruction(canvas_table));
	}
}
