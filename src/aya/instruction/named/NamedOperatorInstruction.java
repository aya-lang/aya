package aya.instruction.named;

import aya.ReprStream;
import aya.StaticData;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.InternalAyaRuntimeException;
import aya.instruction.Instruction;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class NamedOperatorInstruction extends Instruction {

    private final String opName;
    private NamedOperator op = null;

    public NamedOperatorInstruction(SourceStringRef source, String opName) {
        super(source);
        this.opName = opName;
    }

    private void loadOp() {
        if (op != null) {
            return;
        }

        op = StaticData.getInstance().getNamedInstruction(opName);
        if (op == null) {
            throw new InternalAyaRuntimeException(SymbolConstants.NOT_AN_OP_ERROR, "Named instruction :(" + opName + ") does not exist");
        }
    }

    @Override
    public void execute(BlockEvaluator blockEvaluator) {
        this.loadOp();
        op.execute(blockEvaluator);
    }

    @Override
    public ReprStream repr(ReprStream stream) {
        // repr doesn't need to load the OP-instance, we already know the opName.
        stream.print(":(" + opName + ")");
        return stream;
    }
}
