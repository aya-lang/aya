package aya.ext.image;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import aya.Aya;
import aya.exceptions.runtime.ValueError;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.util.DictReader;

public class AyaImage {
	/** Utility class for loading, storing and writing image in Aya */

	private static final SymbolTable symbols = Aya.getInstance().getSymbols();
	private static final Symbol DATA = symbols.getSymbol("data");
	private static final Symbol WIDTH = symbols.getSymbol("width");
	private static final Symbol HEIGHT = symbols.getSymbol("height");

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
			d.getNumberListEx(DATA),
			d.getIntEx(WIDTH),
			d.getIntEx(HEIGHT));
	}
	
	public Dict toDict() {
		Dict d = new Dict();
		d.set(DATA, new List(bytes));
		d.set(WIDTH, Num.fromInt(width));
		d.set(HEIGHT, Num.fromInt(height));
		return d;
	}
	
	public BufferedImage toBufferedImage() {
		if (bytes.length() != (width * height * 3)) {
			throw new ValueError("Error when reading image data. Data is invalid length. Must be width*height*3");
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
