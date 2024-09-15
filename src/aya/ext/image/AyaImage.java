package aya.ext.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

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

	private static final Symbol DATA = SymbolTable.getSymbol("data");
	private static final Symbol WIDTH = SymbolTable.getSymbol("width");
	private static final Symbol HEIGHT = SymbolTable.getSymbol("height");

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
	
	public static AyaImage fromBufferedImage(BufferedImage buf) {
		// get DataBufferBytes from Raster
		WritableRaster raster = buf.getRaster();
		DataBuffer databuf = raster.getDataBuffer();
		int type = databuf.getDataType();
		
		if (type == DataBuffer.TYPE_BYTE) {
			DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
		
			return new AyaImage(
				NumberList.fromBytes(data.getData()),
				buf.getWidth(),
				buf.getHeight());
		} else if (type == DataBuffer.TYPE_INT) {
			DataBufferInt data   = (DataBufferInt) raster.getDataBuffer();
			
			int[] pixels = data.getData();
			byte[] bytes = new byte[data.getSize() * 3];
			
			for (int i = 0; i < data.getSize(); i++) {
				int byte_index = i * 3;
				final Color c = new Color(pixels[i]);
				bytes[byte_index + 0] = (byte)(c.getRed());
				bytes[byte_index + 1] = (byte)(c.getBlue());
				bytes[byte_index + 2] = (byte)(c.getGreen());
			}
				
			return new AyaImage(
				NumberList.fromBytes(bytes),
				buf.getWidth(),
				buf.getHeight());
		} else {
			throw new ValueError("Image buffer type not supported");
		}
	}
	

}
