package aya.ext.graphics;

import aya.ext.graphics.instruction.ArcGraphicsInstruction;
import aya.ext.graphics.instruction.ClearGraphicsInstruction;
import aya.ext.graphics.instruction.ClearRectGraphicsInstruction;
import aya.ext.graphics.instruction.CloseGraphicsInstruction;
import aya.ext.graphics.instruction.CopyRectGraphicsInstruction;
import aya.ext.graphics.instruction.EllipseGraphicsInstruction;
import aya.ext.graphics.instruction.GetPixelsGraphicsInstruction;
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
import aya.ext.graphics.instruction.cursor.ClickEventsInstruction;
import aya.ext.graphics.instruction.cursor.MoveEventsInstruction;
import aya.ext.graphics.instruction.cursor.PressedButtonsInstruction;
import aya.ext.graphics.instruction.keyboard.PressedKeysInstruction;
import aya.ext.graphics.instruction.keyboard.TypedCharsInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class GraphicsInstructionStore implements NamedInstructionStore {

	public static final CanvasTable canvas_table = new CanvasTable();

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new ArcGraphicsInstruction(canvas_table),
				new ClearGraphicsInstruction(canvas_table),
				new ClearRectGraphicsInstruction(canvas_table),
				new ClickEventsInstruction(canvas_table),
				new CloseGraphicsInstruction(canvas_table),
				new CopyRectGraphicsInstruction(canvas_table),
				new EllipseGraphicsInstruction(canvas_table),
				new GetPixelsGraphicsInstruction(canvas_table),
				new IsOpenGraphicsInstruction(canvas_table),
				new LineGraphicsInstruction(canvas_table),
				new ListFontsGraphicsInstruction(canvas_table),
				new MoveEventsInstruction(canvas_table),
				new NewGraphicsInstruction(canvas_table),
				new OvalGraphicsInstruction(canvas_table),
				new PathGraphicsInstruction(canvas_table),
				new PressedKeysInstruction(canvas_table),
				new PressedButtonsInstruction(canvas_table),
				new RectGraphicsInstruction(canvas_table),
				new RotateGraphicsInstruction(canvas_table),
				new RoundRectGraphicsInstruction(canvas_table),
				new SaveGraphicsInstruction(canvas_table),
				new ScaleGraphicsInstruction(canvas_table),
				new SetAlphaGraphicsInstruction(canvas_table),
				new SetBGColorGraphicsInstruction(canvas_table),
				new SetColorGraphicsInstruction(canvas_table),
				new SetColorAlphaGraphicsInstruction(canvas_table),
				new SetFontGraphicsInstruction(canvas_table),
				new SetPaintGradGraphicsInstruction(canvas_table),
				new SetStrokeGraphicsInstruction(canvas_table),
				new SetStrokeWidthGraphicsInstruction(canvas_table),
				new ShearGraphicsInstruction(canvas_table),
				new ShowGraphicsInstruction(canvas_table),
				new TextGraphicsInstruction(canvas_table),
				new TranslateGraphicsInstruction(canvas_table),
				new TypedCharsInstruction(canvas_table),
				new ViewmatGraphicsInstruction(canvas_table)
		);
	}
}
