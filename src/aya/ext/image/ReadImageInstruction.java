package aya.ext.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberItemList;

public class ReadImageInstruction extends NamedInstruction {
	
	public ReadImageInstruction() {
		super("image.read");
		_doc = "Read an image from a file";
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		String filename = null;
		
		try {
			filename = ((Str)a).getStr();
		} catch (ClassCastException e) {
			throw new TypeError(this, "::str", a);
		}
		
		AyaImage image = null;
		
		try {
			image = loadImage(filename);
		} catch (IOException e) {
			throw new AyaRuntimeException(opName() + ", Unable to read image: '" + filename + "'\n" + e.getMessage());
		}
		
		block.push(image.toDict());
	}

	public AyaImage loadImage(String ImageName) throws IOException {
		 // open image
		 File imgPath = new File(ImageName);
		 BufferedImage bufferedImage = ImageIO.read(imgPath);

		 // get DataBufferBytes from Raster
		 WritableRaster raster = bufferedImage .getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return new AyaImage(
				 NumberItemList.fromBytes(data.getData()),
				 bufferedImage.getWidth(),
				 bufferedImage.getHeight());
	}		

}



