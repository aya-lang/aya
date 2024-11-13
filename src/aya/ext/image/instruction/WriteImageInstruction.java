package aya.ext.image.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.InternalAyaRuntimeException;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.ext.image.AyaImage;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolConstants;
import aya.util.DictReader;
import aya.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class WriteImageInstruction extends NamedOperator {
	public WriteImageInstruction() {
		super("image.write");
		_doc = ("image::dict filename::str : write image to file\n"
				+ "  <hint: for a list of supported image formats, see :{image.get_formats}>\n"
				+ "  image::dict : " + AyaImage.getDocString("    ")
				+ "  filename::str : name of the file to write to. The file extension controls the image type."
		);
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj b = blockEvaluator.pop();
		Obj a = blockEvaluator.pop();

		if (!a.isa(Obj.DICT))
			throw new TypeError(this, "::dict", a);
		if (!b.isa(Obj.STR))
			throw new TypeError(this, "::str", b);

		File targetFile = FileUtils.resolveFile(b.str());
		String ext = FileUtils.getExt(targetFile);
		if (ext == null) {
			throw new ValueError("filename for image must have a file extension");
		}

		AyaImage image = new AyaImage(new DictReader((Dict) a, opName() + ".image"));
		final boolean wasWritten;
		try {
			BufferedImage bufImg = image.toBufferedImage();
			wasWritten = ImageIO.write(bufImg, ext, targetFile);
		} catch (Exception e) {
			throw new IOError(opName(), targetFile.getAbsolutePath(), e.getMessage());
		}

		if (!wasWritten) {
			throw new InternalAyaRuntimeException(SymbolConstants.IO_ERR, "found no image writer that supports the requested format '" + ext + "'. Absolute file path: '" + targetFile.getAbsolutePath() + "'");
		}
	}
}
