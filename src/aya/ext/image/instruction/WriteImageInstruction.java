package aya.ext.image.instruction;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.ext.image.AyaImage;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolTable;
import aya.util.DictReader;

public class WriteImageInstruction extends NamedOperator {
	
	public WriteImageInstruction() {
		super("image.write");
		_doc = "Write an image to a file";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		Dict info = null;
		
		try {
			info = (Dict)a;
		} catch (ClassCastException e) {
			throw new TypeError(this, "::dict", a);
		}
		
		DictReader dr = new DictReader(info, opName());
		String filename = dr.getStringEx(SymbolTable.getSymbol("filename"));
		String ext = getExt(filename);
		if (ext.equals("")) {
			throw new ValueError(opName() + ", filename does not have a valid extension");
		}
		
		AyaImage aya_image = AyaImage.fromDict(dr);
		
		try {
			ImageIO.write(aya_image.toBufferedImage(), ext, new File(filename));
		} catch (IOException e) {
			throw new IOError(opName(), filename, e);
		} catch (IllegalArgumentException e) {
			throw new IOError(opName(), filename, e.getMessage());
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
