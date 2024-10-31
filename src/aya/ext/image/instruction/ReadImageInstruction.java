package aya.ext.image.instruction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.ext.image.AyaImage;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;

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

	public AyaImage loadImage(String ImageName) throws IOException {
		 // open image
		 File imgPath = new File(ImageName);
		 BufferedImage bufferedImage = ImageIO.read(imgPath);
		 return AyaImage.fromBufferedImage(bufferedImage);
	}		

}



