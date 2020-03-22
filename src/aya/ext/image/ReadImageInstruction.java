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
import aya.obj.dict.Dict;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.number.Num;
import aya.variable.EncodedVars;

public class ReadImageInstruction extends NamedInstruction {
	
	private static class ImageInfo {
		public byte[] bytes;
		public int width;
		public int height;
		
		public ImageInfo(byte[] bytes, int width, int height) {
			this.bytes = bytes;
			this.width = width;
			this.height = height;
		}
		
		public Dict toDict() {
			Dict d = new Dict();
			d.set(EncodedVars.DATA, NumberItemList.fromBytes(bytes));
			d.set(EncodedVars.WIDTH, Num.fromInt(width));
			d.set(EncodedVars.HEIGHT, Num.fromInt(height));
			return d;
		}
	}
	
	public ReadImageInstruction() {
		super("image.read");
		_doc = "read an image from a file";
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
		
		ImageInfo image = null;
		
		try {
			image = loadImage(filename);
		} catch (IOException e) {
			throw new AyaRuntimeException(opName() + ", Unable to read image: '" + filename + "'");
		}
		
		block.push(image.toDict());
	}

	public ImageInfo loadImage(String ImageName) throws IOException {
		 // open image
		 File imgPath = new File(ImageName);
		 BufferedImage bufferedImage = ImageIO.read(imgPath);

		 // get DataBufferBytes from Raster
		 WritableRaster raster = bufferedImage .getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return new ImageInfo(
				 data.getData(),
				 bufferedImage.getWidth(),
				 bufferedImage.getHeight());
	}		

}



