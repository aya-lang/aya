package aya.ext.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageHelper {
	/**
	 * These Image Types cannot be written, because they require information that isn't available in our model.
	 */
	private static final Set<Integer> unwritableImageTypes = Arrays.stream(new int[]{
			BufferedImage.TYPE_CUSTOM,
			BufferedImage.TYPE_BYTE_BINARY,
			BufferedImage.TYPE_BYTE_INDEXED
	}).boxed().collect(Collectors.toSet());

	public static String getColorSpaceName(ColorSpace colorSpace) {
		int type = colorSpace.getType();
		switch (type) {
			// @formatter:off - this code is long enough as is...
			case ColorSpace.TYPE_XYZ: return "XYZ";
			case ColorSpace.TYPE_Lab: return "Lab";
			case ColorSpace.TYPE_Luv: return "Luv";
			case ColorSpace.TYPE_YCbCr: return "YCbCr";
			case ColorSpace.TYPE_Yxy: return "Yxy";
			case ColorSpace.TYPE_RGB: return "RGB";
			case ColorSpace.TYPE_GRAY: return "GRAY";
			case ColorSpace.TYPE_HSV: return "HSV";
			case ColorSpace.TYPE_HLS: return "HLS";
			case ColorSpace.TYPE_CMYK: return "CMYK";
			case ColorSpace.TYPE_CMY: return "CMY";
			case ColorSpace.TYPE_2CLR: return "2CLR";
			case ColorSpace.TYPE_3CLR: return "3CLR";
			case ColorSpace.TYPE_4CLR: return "4CLR";
			case ColorSpace.TYPE_5CLR: return "5CLR";
			case ColorSpace.TYPE_6CLR: return "6CLR";
			case ColorSpace.TYPE_7CLR: return "7CLR";
			case ColorSpace.TYPE_8CLR: return "8CLR";
			case ColorSpace.TYPE_9CLR: return "9CLR";
			case ColorSpace.TYPE_ACLR: return "ACLR";
			case ColorSpace.TYPE_BCLR: return "BCLR";
			case ColorSpace.TYPE_CCLR: return "CCLR";
			case ColorSpace.TYPE_DCLR: return "DCLR";
			case ColorSpace.TYPE_ECLR: return "ECLR";
			case ColorSpace.TYPE_FCLR: return "FCLR";
			default: return "Unknown";
			// @formatter:on
		}
	}

	/**
	 * Creates a compatible (possibly non-standard) Image.
	 */
	public static BufferedImage createCompatibleImage(int width, int height, ImageMeta meta) {
		Integer nativeImageType = findNativeImageType(meta);
		if (nativeImageType != null) {
			return new BufferedImage(width, height, nativeImageType);
		}

		ComponentColorModel colorModel = meta.isGray ? createGrayComponentModel(meta) : createComponentModel(meta);
		int numComponents = colorModel.getNumComponents();
		int[] bOffs = IntStream.range(0, numComponents).toArray();
		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, width * numComponents, numComponents, bOffs, null);
		return new BufferedImage(colorModel, raster, false, null);
	}

	/**
	 * Tries to find the native ImageType that supports the channels and metadata.
	 * <p> Possible image types are
	 * <p> - {@link BufferedImage#TYPE_BYTE_GRAY}
	 * <p> - {@link BufferedImage#TYPE_INT_ARGB}
	 * <p> - {@link BufferedImage#TYPE_INT_ARGB_PRE}
	 * <p> - {@link BufferedImage#TYPE_3BYTE_BGR}
	 *
	 * @return a compatible ImageType | or null if no matching image type was found
	 */
	private static Integer findNativeImageType(ImageMeta meta) {
		if (meta.javaImageType != null && !unwritableImageTypes.contains(meta.javaImageType)) {
			return meta.javaImageType;
		}

		/* Properties to compare against java.awt.image.BufferedImage#BufferedImage(int, int, int):
			- grayscale
			- alpha
			- premultiplied
		 */

		if (meta.isGray) {
			// TYPE_BYTE_GRAY
			if (!meta.premultiplied) return null;
			if (meta.hasAlpha) return null;
			return BufferedImage.TYPE_BYTE_GRAY;
		}

		if (meta.hasAlpha) {
			return meta.premultiplied ? BufferedImage.TYPE_INT_ARGB_PRE : BufferedImage.TYPE_INT_ARGB;
		}

		// if no alpha is present, 'premultiplied' is meaningless
		return BufferedImage.TYPE_3BYTE_BGR; // seems to be the preferred over 'INT_RGB', at least on Windows.
	}

	private static ComponentColorModel createGrayComponentModel(ImageMeta meta) {
		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		boolean hasAlpha = meta.hasAlpha;
		int alphaBits = hasAlpha ? 8 : 0;
		int[] nBits = new int[1 + (hasAlpha ? 1 : 0)];
		Arrays.fill(nBits, 8);

		return new ComponentColorModel(colorSpace, nBits, hasAlpha, meta.premultiplied, getTransparency(alphaBits), DataBuffer.TYPE_BYTE);
	}

	private static ComponentColorModel createComponentModel(ImageMeta meta) {
		boolean hasAlpha = meta.hasAlpha;
		int alphaBits = hasAlpha ? 8 : 0;
		int[] nBits = new int[3 + (hasAlpha ? 1 : 0)];
		Arrays.fill(nBits, 8);

		return new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB),
				nBits, hasAlpha, meta.premultiplied,
				getTransparency(alphaBits),
				DataBuffer.TYPE_BYTE
		);
	}

	public static int getTransparency(int alphaBits) {
		if (alphaBits == 0) return Transparency.OPAQUE;
		if (alphaBits == 1) return Transparency.BITMASK;
		return Transparency.TRANSLUCENT;
	}

}
