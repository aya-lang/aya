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

public class UTF16ToTuplesInstruction extends NamedOperator {

	public UTF16ToTuplesInstruction() {
		super("utf16.to_tuples");
		_doc = "(L<J>|L<S>|L<C>) convert a list of symbols/strings/chars into 2-tuples using utf16 surrogate pairs (return L<S>)";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj l = blockEvaluator.pop();
		final List symbols;
		if (l.isa(Obj.LIST)) {
			symbols = Casting.asList(l);
		} else {
			throw new TypeError(this, "L", l);
		}

		ArrayList<String> symbolStrings = IntStream.range(0, symbols.length())
				.mapToObj(i -> {
					Obj symbol = symbols.getExact(i);
					if (symbol.isa(Obj.SYMBOL)) {
						return Casting.asSymbol(symbol).name();
					} else if (symbol.isa(Obj.STR)) {
						return Casting.asStr(symbol).str();
					} else if (symbol.isa(Obj.CHAR)) {
						return Casting.asChar(symbol).str();
					} else {
						throw new TypeError("TypeError at " + this._name + " l.[" + i + "]. Expected S|J|C, found " + symbol.repr());
					}
				}).collect(Collectors.toCollection(ArrayList::new));

		ArrayList<String> result = new ArrayList<>();
		for (int i = 0; i < symbolStrings.size(); i++) {
			String symbolStr1 = symbolStrings.get(i);
			if (viableSurrogateChar(symbolStr1) && (i + 1) < symbolStrings.size()) {
				// maybe this and the next symbol can be combined into a tuple
				i++;
				String symbolStr2 = symbolStrings.get(i);
				if (viableSurrogateChar(symbolStr2)) {
					result.add(UTF16.surrogateToStr(
							((UTF16.highSurrogateBase | (symbolStr1.charAt(0) & 0b11_1111_1111)) << 16)
									| (UTF16.lowSurrogateBase | (symbolStr2.charAt(0) & 0b11_1111_1111))
					));
				} else {
					result.add(symbolStr1);
					result.add(symbolStr2);
				}
			} else {
				// too long/short to be combined with another character
				result.add(symbolStr1);
			}
		}

		ArrayList<Obj> strList = result.stream().map(List::fromString).collect(Collectors.toCollection(ArrayList::new));
		blockEvaluator.push(new List(strList));
	}

	private static boolean viableSurrogateChar(String symbol) {
		if (symbol.length() != 1)
			return false; // too long/short to be combined with another character

		int c = symbol.charAt(0) & 0xffff;
		if (UTF16.isSurrogate(c))
			return false; // already a surrogate (this *should* be impossible since these characters are forbidden by the Unicode standard)

		if ((c & 0b1111_1100_0000_0000) != 0)
			return false; // cannot be represented with only 10 bits

		return true;
	}
}
