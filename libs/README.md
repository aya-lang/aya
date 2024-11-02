## Adding more Instructions

You can add more Instructions by placing `.jar` files here.

A jar can provide one or more `NamedInstructionStore` implementations.  
This is done by adding the fully qualified class name(s) to this file:
`META-INF/services/aya.instruction.named.NamedInstructionStore`

For more information, you should look up 'Java SPI' (Service Provider Interface).

### Example

This adds an instruction `:{example.instruction}` which pushes the String `hello, world` onto the stack.
```java
package my.instruction;

import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.list.List;

public class MyInstructionStore implements NamedInstructionStore {
	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new NamedOperator("example.instruction") {
					@Override
					public void execute(BlockEvaluator blockEvaluator) {
						blockEvaluator.push(List.fromString("hello, world"));
					}
				}
		);
	}
}
```

Then you can register your implementation as a ServiceProvider like this:
```
> cat META-INF/services/aya.instruction.named.NamedInstructionStore
my.instruction.MyInstructionStore
```