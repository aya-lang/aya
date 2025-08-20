package aya.ext.unicode;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.util.Casting;
import aya.util.UTF16;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UTF16FromTuplesInstruction extends NamedOperator {

	public UTF16FromTuplesInstruction() {
		super("utf16.from_tuples");
		_doc = "(S|L<S>) unpack a list of tuple-strings to their corresponding variables using utf16 surrogate pairs (return L<S>)";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj l = blockEvaluator.pop();
		final List tuples;
		if (l.isa(Obj.STR)) {
			ArrayList<Obj> strList = new ArrayList<>();
			strList.add(List.fromStr(Casting.asStr(l)));
			tuples = new List(strList);
		} else if (l.isa(Obj.LIST)) {
			tuples = Casting.asList(l);
		} else {
			throw new TypeError(this, "S|L", l);
		}

		ArrayList<Obj> result = IntStream.range(0, tuples.length())
				.mapToObj(i -> {
					Obj symbol = tuples.getExact(i);
					if (symbol.isa(Obj.STR)) {
						return Casting.asStr(symbol).str();
					} else {
						throw new TypeError("TypeError at " + this._name + " l.[" + i + "]. Expected S, found " + symbol.repr());
					}
				})
				.flatMap(tuple -> {
					if (tuple.length() == 2 && UTF16.isHighSurrogate(tuple.charAt(0))) {
						String highSurrogate = "" + ((char) (tuple.charAt(0) & 0b11_1111_1111));
						String lowSurrogate = "" + ((char) (tuple.charAt(1) & 0b11_1111_1111));
						return Stream.of(highSurrogate, lowSurrogate);
					} else {
						return Stream.of(tuple);
					}
				})
				.map(List::fromString)
				.collect(Collectors.toCollection(ArrayList::new));

		blockEvaluator.push(new List(result));
	}
}
