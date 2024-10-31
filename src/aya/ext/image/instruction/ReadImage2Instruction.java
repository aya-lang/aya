package aya.ext.image.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.ext.image.AyaImage2;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReadImage2Instruction extends NamedOperator {

	public ReadImage2Instruction() {
		super("image.read2");
		_doc = ("Read an image from a file\n"
				+ "  <hint: for a list of supported image formats, see :{image.get_formats}>\n"
				+ "  <returns: " + AyaImage2.getDocString("    ") + ">"
		);
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		if (!a.isa(Obj.STR)) {
			throw new TypeError(this, "::str", a);
		}

		String fileName = a.str();
		AyaImage2 image;
		try {
			BufferedImage readImg = ImageIO.read(new File(fileName));
			if (readImg == null)
				throw new IOError(opName(), fileName, "unsupported image format");

			image = new AyaImage2(readImg);
		} catch (IOException e) {
			throw new IOError(opName(), fileName, e.getMessage());
		}

		blockEvaluator.push(image.toDict());
	}
}
