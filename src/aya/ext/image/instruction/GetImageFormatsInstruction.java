package aya.ext.image.instruction;

import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.Str;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class GetImageFormatsInstruction extends NamedOperator {

	private static final IIORegistry imageIoRegistry = IIORegistry.getDefaultInstance();

	public GetImageFormatsInstruction() {
		super("image.get_formats");
		_doc = (": finds the image formats that are supported by your platform\n"
				+ "  <hint: always supported: bmp, gif, jpeg/jpg, png, wbmp>\n"
				+ "  <hint: since java 11: tif/tiff>\n"
				+ "  <returns: list of supported file extensions>"
		);
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		ArrayList<Obj> allExtensions = new ArrayList<>();

		Iterator<ImageWriterSpi> imageWriters = imageIoRegistry.getServiceProviders(ImageWriterSpi.class, false);
		while (imageWriters.hasNext()) {
			String[] extensions = imageWriters.next().getFileSuffixes();
			if (extensions == null) {
				continue;
			}

			allExtensions.addAll(Arrays.stream(extensions).map(ext -> new List(new Str(ext))).collect(Collectors.toList()));
		}

		blockEvaluator.push(new List(allExtensions));
	}
}
