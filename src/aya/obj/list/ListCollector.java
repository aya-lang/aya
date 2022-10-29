package aya.obj.list;

import aya.obj.Obj;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ListCollector implements Collector<Obj, ArrayList<Obj>, List> {

	@Override
	public Supplier<ArrayList<Obj>> supplier() {
		return ArrayList::new;
	}

	@Override
	public BiConsumer<ArrayList<Obj>, Obj> accumulator() {
		return ArrayList::add;
	}

	@Override
	public BinaryOperator<ArrayList<Obj>> combiner() {
		return (left, right) -> {
			left.addAll(right);
			return left;
		};
	}

	@Override
	public Function<ArrayList<Obj>, List> finisher() {
		return List::new;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}
}
