package example;

import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.list.Str;

import java.util.Collection;
import java.util.List;

public class ExampleStore implements NamedInstructionStore {
    private static DataStorage data;

    @Override
    public Collection<NamedOperator> getNamedInstructions() {
        return List.of(
                new OpPut(),
                new OpGet()
        );
    }

    private static class OpPut extends NamedOperator {
        public OpPut() {
            super("example.put");
        }

        @Override
        public void execute(BlockEvaluator blockEvaluator) {
            data = new DataStorage(blockEvaluator.pop().str());
        }
    }

    private static class OpGet extends NamedOperator {
        public OpGet() {
            super("example.get");
        }

        @Override
        public void execute(BlockEvaluator blockEvaluator) {
            blockEvaluator.push(aya.obj.list.List.fromStr(new Str(data.value)));
        }
    }

    /**
     * Use an extra class for this to verify that the class (/classloader) is not unloaded
     */
    private static class DataStorage {
        String value;

        public DataStorage(String value) {
            this.value = value;
        }
    }
}