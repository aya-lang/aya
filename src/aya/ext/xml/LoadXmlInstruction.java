package aya.ext.xml;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;

public class LoadXmlInstruction extends NamedOperator {
    public LoadXmlInstruction() {
        super("xml.loads");
        _doc = "Convert an Xml string to a dict\n"
                + "  <returns: " + AyaXmlNode.getDocString("    ") + ">\n"
                + "  <note: entity references are not fully supported>"; // the parser seems to flatten the resolved value into the textContent of the next node. A workaround isn't worth the hassle.
    }

    @Override
    public void execute(BlockEvaluator blockEvaluator) {
        final Obj a = blockEvaluator.pop();

        if (a.isa(Obj.STR)) {
            blockEvaluator.push(AyaXmlNode.fromString(a.str()).toDict());
        } else {
            throw new TypeError(this, "::str", a);
        }
    }
}
