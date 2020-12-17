package aya.ext.image;

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
		_doc = "Write an image to a file";
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
		String filename = dr.getStringEx(EncodedVars.FILENAME);
		String ext = getExt(filename);
		if (ext.equals("")) {
			throw new AyaRuntimeException(opName() + ", filename does not have a valid extension");
		}
		
		AyaImage aya_image = AyaImage.fromDict(dr);
		
		try {
			ImageIO.write(aya_image.toBufferedImage(), ext, new File(filename));
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

}
