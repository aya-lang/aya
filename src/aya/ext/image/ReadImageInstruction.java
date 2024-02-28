package aya.ext.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.BlockEvaluator;
import aya.obj.list.numberlist.NumberList;

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

		 // get DataBufferBytes from Raster
		 WritableRaster raster = bufferedImage .getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return new AyaImage(
				 NumberList.fromBytes(data.getData()),
				 bufferedImage.getWidth(),
				 bufferedImage.getHeight());
	}		

}



