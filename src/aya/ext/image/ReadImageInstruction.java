package aya.ext.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.util.FileUtils;

public class ReadImageInstruction extends NamedOperator {
	
	public ReadImageInstruction() {
		super("image.read");
		_doc = "Read an image from a file";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		String filename = null;
		
		if (a.isa(Obj.STR)) {
			filename = a.str();
		} else {
			throw new TypeError(this, "::str", a);
		}
		
		AyaImage image = null;
		
		try {
			image = loadImage(filename);
		} catch (IOException e) {
			throw new IOError(opName(), filename, e.getMessage());
		}
		
		blockEvaluator.push(image.toDict());
	}

	public AyaImage loadImage(String imageName) throws IOException {
		// open image
		File imgFile = FileUtils.resolveFile(imageName);
		BufferedImage bufferedImage = ImageIO.read(imgFile);
		return AyaImage.fromBufferedImage(bufferedImage);
	}		

}



