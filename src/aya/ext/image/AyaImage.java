package aya.ext.image;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import aya.exceptions.AyaRuntimeException;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.util.DictReader;
import aya.variable.EncodedVars;

public class AyaImage {
	/** Utility class for loading, storing and writing image in Aya */
	
	private NumberList bytes;
	private int width;
	private int height;
	
	public AyaImage(NumberList bytes, int width, int height) {
		this.bytes = bytes;
		this.width = width;
		this.height = height;
	}
	
	public static AyaImage fromDict(DictReader d) {
		return new AyaImage(
			d.getNumberListEx(EncodedVars.DATA),
			d.getIntEx(EncodedVars.WIDTH),
			d.getIntEx(EncodedVars.HEIGHT));
	}
	
	public Dict toDict() {
		Dict d = new Dict();
		d.set(EncodedVars.DATA, new List(bytes));
		d.set(EncodedVars.WIDTH, Num.fromInt(width));
		d.set(EncodedVars.HEIGHT, Num.fromInt(height));
		return d;
	}
	
	public BufferedImage toBufferedImage() {
		if (bytes.length() != (width * height * 3)) {
			throw new AyaRuntimeException("Error when reading image data. Data is invalid length. Must be width*height*3");
		}
		
		byte[] raw = bytes.toByteArray();

		DataBuffer buffer = new DataBufferByte(raw, raw.length);
		SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, width*3, new int[]{2,1,0});
		Raster raster = Raster.createRaster(sampleModel, buffer, null);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		image.setData(raster);
		
		return image;
	}
	

}
