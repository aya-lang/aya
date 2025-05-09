package aya.ext.xml;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.util.Casting;
import aya.util.DictReader;

public class ToXmlInstruction extends NamedOperator {
    public ToXmlInstruction() {
        super("xml.dumps");
        _doc = "Serialize a dict to an xml string";
    }

    @Override
    public void execute(BlockEvaluator blockEvaluator) {
        final Obj a = blockEvaluator.pop();

        if (!a.isa(Obj.DICT)) {
            throw new TypeError(this, "::dict", a);
        }

        blockEvaluator.push(List.fromString(new AyaXmlNode(new DictReader(Casting.asDict(a), "xmlDict")).toXmlString()));
    }
}
