package aya.ext.sys;

import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class FileExistsSystemInstruction extends NamedOperator {

	public FileExistsSystemInstruction() {
		super("sys.file_exists");
		_doc = "test if the file exists";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj arg = blockEvaluator.pop();
		blockEvaluator.push(FileUtils.isFile(arg.str()) ? Num.ONE : Num.ZERO);
	}

}
