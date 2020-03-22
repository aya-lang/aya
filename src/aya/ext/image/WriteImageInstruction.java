package aya.ext.image;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import aya.Aya;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.util.DictReader;
import aya.variable.EncodedVars;

public class WriteImageInstruction extends NamedInstruction {
	
	public WriteImageInstruction() {
		super("image.write");
		_doc = "write an image to a file";
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		Dict info = null;
		
		try {
			info = (Dict)a;
		} catch (ClassCastException e) {
			throw new TypeError(this, "::dict", a);
		}
		
		DictReader dr = new DictReader(info, opName());
		String filename = dr.getString(EncodedVars.FILENAME);
		String ext = getExt(filename);
		if (ext.equals("")) {
			throw new AyaRuntimeException(opName() + ", filename does not have a valid extension");
		}
		
		try {
			writeImage(filename,
					   ext,
					   dr.getNumberListEx(EncodedVars.DATA).toByteArray(),
					   dr.getIntEx(EncodedVars.WIDTH), 
					   dr.getIntEx(EncodedVars.HEIGHT));
		} catch (IOException e) {
			e.printStackTrace(Aya.getInstance().getErr());
			throw new AyaRuntimeException(opName() + ", unable to write image to file'" + filename + "'");
		} catch (IllegalArgumentException e) {
			e.printStackTrace(Aya.getInstance().getErr());
			throw new AyaRuntimeException(opName() + ", unable to write image to file'" + filename + "'");
		}
	}
	
	public String getExt(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i+1);
		}
		return extension;
	}

	public void writeImage(String filename, String ext, byte[] bytes, int width, int height) throws IOException {
		if (bytes.length != (width * height * 3)) {
			throw new AyaRuntimeException(opName() + ", Error when writing file '" + filename + "' data is invalid length. Must be width*height*3");
		}

		DataBuffer buffer = new DataBufferByte(bytes, bytes.length);

		SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, width*3, new int[]{2,1,0});

		Raster raster = Raster.createRaster(sampleModel, buffer, null);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		image.setData(raster);
		ImageIO.write(image, ext, new File(filename));
	}		

}
