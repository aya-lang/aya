package aya.ext.xml;

import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class XmlInstructionStore implements NamedInstructionStore {

    @Override
    public Collection<NamedOperator> getNamedInstructions() {
        return Arrays.asList(
                new ToXmlInstruction(),
                new LoadXmlInstruction()
        );
    }
}
