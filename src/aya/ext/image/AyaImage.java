package aya.ext.image;

import aya.StaticData;
import aya.exceptions.runtime.ValueError;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.util.DictReader;
import aya.util.Sym;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements the data-type for Image instructions:
 * <pre>{@code
 * {,
 *     .# meta information about the image.
 *     .# :(image.read) provides this information
 *     .# :(image.write) infers these values unless specified
 *     {,
 *         <bool> :gray
 *         <bool> :alpha
 *         <bool> :premultiplied
 *         <num>  :java_image_type
 *     } :meta
 *
 *     <num> :width
 *     <num> :height
 *
 *     .# each channel is backed by a byte[] internally
 *     256.R :r
 *     256.R :g
 *     256.R :b
 *     256.R :a
 * }}</pre>
 */
public class AyaImage
{
	private static final Symbol SYM_META = Sym.sym("meta");
	private static final Symbol SYM_WIDTH = Sym.sym("width");
	private static final Symbol SYM_HEIGHT = Sym.sym("height");

	public static String getDocString(String padLeft) {
		return ("image\n"
				+ padLeft + "meta::dict : " + ImageMeta.getDocString(padLeft + "    ")
				+ padLeft + "width::num : width in pixels\n"
				+ padLeft + "height::num : height in pixels\n"
				+ padLeft + "<hint: all channels are optional>\n"
				+ padLeft + "<hint: channels are iterated in row-major order>\n"
				+ padLeft + "<hint: all channel values are scaled to range [0 255]>\n"
				+ padLeft + Channel.red.symbol.name() + "::list : red channel\n"
				+ padLeft + Channel.green.symbol.name() + "::list : green channel\n"
				+ padLeft + Channel.blue.symbol.name() + "::list : blue channel\n"
				+ padLeft + Channel.alpha.symbol.name() + "::list : alpha channel\n"
		);
	}

	public final ImageMeta imageMeta;
	public final int width;
	public final int height;
	public final Map<Channel, byte[]> channels = new EnumMap<>(Channel.class);

	public AyaImage(DictReader d) {
		imageMeta = new ImageMeta(d.getDictReader(SYM_META), d.hasKey(Channel.alpha.symbol));

		for (Channel channel : Channel.values()) {
			if (!d.hasKey(channel.symbol))
				continue;

			NumberList valueList = d.getNumberListEx(channel.symbol);
			channels.put(channel, valueList.toByteArray());
		}
		if (channels.isEmpty()) {
			throw new ValueError(d.get_err_name() + ": must have at least one channel (::r, ::g, ::b, ::a)");
		}
		// verify that all channels have the same length (number of pixels)
		int[] channelLengths = channels.values().stream().mapToInt(c -> c.length).distinct().toArray();
		if (channelLengths.length != 1) {
			// construct a String that contains the lengths of each channel. example: 'r=100, g=100, b=100, a=101'
			String lenStr = channels.entrySet().stream().map(e -> e.getKey().symbol.name() + "=" + e.getValue().length).collect(Collectors.joining(", "));
			throw new ValueError(d.get_err_name() + ": inconsistent channel lengths, found: " + lenStr);
		}
		int numPixels = channelLengths[0];

		boolean hasWidth = d.hasKey(SYM_WIDTH);
		boolean hasHeight = d.hasKey(SYM_HEIGHT);
		if (!hasWidth && !hasHeight) {
			throw new ValueError(d.get_err_name() + ": must have at either ::width, ::height or both");
		}

		// verify that the width*height matches the number of pixels.
		if (hasWidth && hasHeight) {
			this.width = d.getIntEx(SYM_WIDTH);
			this.height = d.getIntEx(SYM_HEIGHT);
			if ((width * height) != numPixels) {
				throw new ValueError(d.get_err_name() + ": number of pixels (" + numPixels + ") does not match width*height (" + width + "x" + height + ")");
			}
		} else if (hasWidth) {
			this.width = d.getIntEx(SYM_WIDTH);
			if (numPixels % width != 0) {
				throw new ValueError(d.get_err_name() + ": if ::height is omitted, the number of pixels (" + numPixels + ") must be a multiple of the width (" + width + ")");
			}
			this.height = numPixels / width;
		} else {
			this.height = d.getIntEx(SYM_HEIGHT);
			if (numPixels % height != 0) {
				throw new ValueError(d.get_err_name() + ": if ::width is omitted, the number of pixels (" + numPixels + ") must be a multiple of the height (" + height + ")");
			}
			this.width = numPixels / height;
		}
	}

	public AyaImage(BufferedImage image) {
		ColorModel model = image.getColorModel();
		ColorSpace colorSpace = model.getColorSpace();

		if (colorSpace.getType() != ColorSpace.TYPE_RGB
				&& colorSpace.getType() != ColorSpace.TYPE_GRAY
				&& colorSpace.getType() != ColorSpace.TYPE_3CLR
				&& colorSpace.getType() != ColorSpace.TYPE_4CLR
		) {
			StaticData.IO.err().println("Info: ColorSpace '" + ImageHelper.getColorSpaceName(colorSpace) + "' will be converted to RGB");
		}

		/* One of the downsides of guaranteeing all color values to be integers in range [0, 255]
		 * is that higher bit depths are not supported (e.g. 16 bit grayscale).
		 * I think this is an acceptable limitation, since java.awt.Color also only supports 8-bit colors.
		 */
		int[] bitWidths = model.getComponentSize();
		for (int i = 0; i < bitWidths.length; i++) {
			if (bitWidths[i] > 8) {
				StaticData.IO.err().println("Warning: Color component '" + colorSpace.getName(i) + "' is " + bitWidths[i] + " bits wide. Maximum supported is 8. The data will be truncated.");
			}
		}

		this.imageMeta = new ImageMeta(image);
		this.width = image.getWidth();
		this.height = image.getHeight();

		int numColorChannels = model.getNumColorComponents();
		if (numColorChannels <= 0) {
			throw new ValueError("Image has no color channels"); // I don't think this is possible in practice. See java.awt.color.ICC_Profile#getNumComponents
		}

		byte[] red = new byte[width * height];
		byte[] green = imageMeta.isGray ? null : new byte[width * height];
		byte[] blue = imageMeta.isGray ? null : new byte[width * height];
		byte[] alpha = new byte[width * height];
		boolean hasAlpha = model.hasAlpha();

		int pixelIdx = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// getRGB takes care of all abstractions (such as premultiplied alpha, transfer types (byte/short/int), colorModel conversions, ...)
				// despite the name, it also provides alpha information
				int argb = image.getRGB(x, y);

				red[pixelIdx] = (byte) (argb >> 16);
				if (green != null) green[pixelIdx] = (byte) (argb >> 8);
				if (blue != null) blue[pixelIdx] = (byte) argb;
				alpha[pixelIdx] = hasAlpha ? ((byte) (argb >> 24)) : ((byte) 255); // ComponentColorModel and DirectColorModel already behave like this, but IndexedColorModel does not.

				pixelIdx++;
			}
		}

		this.channels.put(Channel.red, red);
		this.channels.put(Channel.alpha, alpha);

		if (imageMeta.isGray) {
			this.channels.put(Channel.green, red);
			this.channels.put(Channel.blue, red);
		} else {
			this.channels.put(Channel.green, green);
			this.channels.put(Channel.blue, blue);
		}
	}

	public Dict toDict() {
		Dict d = new Dict();
		d.set(SYM_META, imageMeta.toDict());
		d.set(SYM_WIDTH, Num.fromInt(width));
		d.set(SYM_HEIGHT, Num.fromInt(height));
		for (Map.Entry<Channel, byte[]> entry : channels.entrySet()) {
			d.set(entry.getKey().symbol, new List(NumberList.fromUBytes(entry.getValue())));
		}
		return d;
	}

	public BufferedImage toBufferedImage() {
		BufferedImage image = ImageHelper.createCompatibleImage(width, height, imageMeta);

		byte[] red = channels.get(Channel.red);
		byte[] green = channels.get(imageMeta.isGray ? Channel.red : Channel.green);
		byte[] blue = channels.get(imageMeta.isGray ? Channel.red : Channel.blue);
		byte[] alpha = channels.get(Channel.alpha);

		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = (alpha == null ? 0 : ((alpha[i] & 0xff) << 24))
						| (red == null ? 0 : ((red[i] & 0xff) << 16))
						| (green == null ? 0 : ((green[i] & 0xff) << 8))
						| (blue == null ? 0 : (blue[i] & 0xff));
				image.setRGB(x, y, rgb);
				i++;
			}
		}
		return image;
	}

}
