package aya.ext.image;

import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.util.DictReader;
import aya.util.Sym;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class ImageMeta {
	private static final Symbol SYM_GRAY = Sym.sym("gray");
	private static final Symbol SYM_ALPHA = Sym.sym("alpha");
	private static final Symbol SYM_PREMULTIPLIED = Sym.sym("premultiplied");
	private static final Symbol SYM_JAVA_IMAGE_TYPE = Sym.sym("java_image_type");

	public static String getDocString(String padLeft) {
		return ("image encoding metadata\n"
				+ padLeft + "gray::num (bool) : image is grayscale. default=false\n"
				+ padLeft + "  <hint: when reading grayscale images, the red channel is copied into the green and blue channels>\n"
				+ padLeft + "  <hint: when writing grayscale images, green and blue channels are ignored>\n"
				+ padLeft + "alpha::num (bool) : image supports alpha. default=inferred\n"
				+ padLeft + "  <hint: when reading images without alpha, the alpha channel values are always 255>\n"
				+ padLeft + "  <hint: when writing images without alpha, the alpha channel is ignored (255 is assumed)>\n"
				+ padLeft + "premultiplied::num (bool) : alpha is premultiplied. default=false\n"
				+ padLeft + "java_image_type::num (int) : optional image type. default=inferred. See https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html\n"
		);
	}

	public final boolean isGray;
	public final boolean hasAlpha;
	public final boolean premultiplied;
	public final Integer javaImageType;

	public ImageMeta(DictReader d, boolean inferredAlpha) {
		this.isGray = d.getBool(SYM_GRAY, false);
		this.hasAlpha = d.getBool(SYM_ALPHA, inferredAlpha);
		this.premultiplied = d.getBool(SYM_PREMULTIPLIED, false);
		this.javaImageType = d.hasKey(SYM_JAVA_IMAGE_TYPE) ? d.getIntEx(SYM_JAVA_IMAGE_TYPE) : null;
	}

	public ImageMeta(BufferedImage image) {
		ColorModel model = image.getColorModel();
		ColorSpace colorSpace = model.getColorSpace();
		this.isGray = colorSpace.getType() == ColorSpace.TYPE_GRAY;
		this.hasAlpha = model.hasAlpha();
		this.premultiplied = model.isAlphaPremultiplied();
		this.javaImageType = image.getType() == BufferedImage.TYPE_CUSTOM ? null : image.getType();
	}

	public Dict toDict() {
		Dict d = new Dict();
		d.set(SYM_GRAY, Num.fromBool(isGray));
		d.set(SYM_ALPHA, Num.fromBool(hasAlpha));
		d.set(SYM_PREMULTIPLIED, Num.fromBool(premultiplied));
		if (javaImageType != null) {
			d.set(SYM_JAVA_IMAGE_TYPE, Num.fromInt(javaImageType));
		}
		return d;
	}

	@Override
	public String toString() {
		return "ImageMeta{" +
				"isGray=" + isGray +
				", hasAlpha=" + hasAlpha +
				", premultiplied=" + premultiplied +
				", javaImageType=" + javaImageType +
				'}';
	}
}
