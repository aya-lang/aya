package aya.ext.image.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.ext.image.AyaImage2;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.util.DictReader;
import aya.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class WriteImage2Instruction extends NamedOperator {
	public WriteImage2Instruction() {
		super("image.write2");
		_doc = ("image::dict filename::str : write image to file\n"
				+ "  <hint: for a list of supported image formats, see :{image.get_formats}>\n"
				+ "  image::dict : " + AyaImage2.getDocString("    ")
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

		AyaImage2 image = new AyaImage2(new DictReader((Dict) a, opName() + ".image"));
		try {
			BufferedImage bufImg = image.toBufferedImage();
			boolean wasWritten = ImageIO.write(bufImg, ext, targetFile);
			if (!wasWritten) {
				throw new IOError(opName(), targetFile.getAbsolutePath(), "found no image writer that supports the requested format");
			}
		} catch (Exception e) {
			throw new IOError(opName(), targetFile.getAbsolutePath(), e.getMessage());
		}
	}
}
