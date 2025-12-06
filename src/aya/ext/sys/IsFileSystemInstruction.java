package aya.ext.sys;

import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class IsFileSystemInstruction extends NamedOperator {

	public IsFileSystemInstruction() {
		super("sys.isfile");
		_doc = "test if the file exists and is a regular file";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj arg = blockEvaluator.pop();
		blockEvaluator.push(FileUtils.isFile(arg.str()) ? Num.ONE : Num.ZERO);
	}

}
