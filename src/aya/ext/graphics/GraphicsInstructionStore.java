package aya.ext.graphics;

import aya.instruction.named.NamedInstructionStore;

public class GraphicsInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		
		CanvasInterface canvas_interface = new CanvasInterface();
		
		// JSON
		addInstruction(new LegacyGraphicsInstruction(canvas_interface));
		addInstruction(new ViewmatGraphicsInstruction(canvas_interface));
	}
}
