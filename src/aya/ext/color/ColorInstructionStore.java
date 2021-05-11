package aya.ext.color;

import java.awt.Color;

import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.SymbolConstants;
import aya.util.ColorFactory;

public class ColorInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {

		addInstruction(new NamedInstruction("color.fromstr", "color::str: convert an html valid color to an rgba dict") {
			@Override
			public void execute(Block block) {
				String color_str = block.pop().str();
				try {
					Color color = ColorFactory.web(color_str);
					Dict d = new Dict();
					d.set(SymbolConstants.R, Num.fromInt(color.getRed()));
					d.set(SymbolConstants.G, Num.fromInt(color.getGreen()));
					d.set(SymbolConstants.B, Num.fromInt(color.getBlue()));
					d.set(SymbolConstants.A, Num.fromInt(color.getAlpha()));
					block.push(d);
				} catch (IllegalArgumentException e) {
					throw new ValueError(":{color.fromstr} Invalid color: '" + color_str + "'");
				}
			}
		});

		addInstruction(new NamedInstruction("color.name_list", "return a list of all named colors") {
			@Override
			public void execute(Block block) {
				List named_colors = new List();
				for (String s : ColorFactory.listNamedColors()) {
					named_colors.mutAdd(List.fromString(s));
				}
				block.push(named_colors);
			}
		});

	}
}
