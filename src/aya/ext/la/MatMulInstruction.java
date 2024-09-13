package aya.ext.la;

import java.util.ArrayList;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.NumberMath;
import aya.util.Casting;

public class MatMulInstruction extends NamedOperator {
	
	public MatMulInstruction() {
		super("la.mul");
		_doc = ("2d matrix multiplication");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj b = blockEvaluator.pop();
		Obj a = blockEvaluator.pop();

		if (a.isa(Obj.LIST) && b.isa(Obj.LIST)) {
			blockEvaluator.push(matMulSafe(Casting.asList(a), Casting.asList(b)));
		} else {
			throw new TypeError(this, "LL", b, a);
		}
	}
	
	private List matMulSafe(List a, List b) {
		try {
			return matMul(a, b);
		} catch (ClassCastException e0) {
			throw new TypeError(this, "LL", b, a);
		} catch (IndexError e1) {
			throw new ValueError("Dimension error in :{la.mul}");
		} catch (IndexOutOfBoundsException e2) {
			throw new ValueError("Dimension error in :{la.mul}");
		}
	}
	
	private static ArrayList<NumberList> newEmpty(int rows, int cols) {
		ArrayList<NumberList> out = new ArrayList<NumberList>();
		for (int r = 0; r < rows; r++) {
			out.add(NumberList.repeat(Num.ZERO, cols));
		}
		return out;
	}

	private static ArrayList<DoubleList> newEmptyDoubleList(int rows, int cols) {
		ArrayList<DoubleList> out = new ArrayList<DoubleList>();
		for (int r = 0; r < rows; r++) {
			out.add(new DoubleList(0.0, cols));
		}
		return out;
	}
	
	private static List matMul(List a, List b) {
		// A is (m x n)
		int m = a.length();
		//int n = Casting.asList(a.getExact(0)).length();
		// B in (p x q)
		int p = b.length();
		int q = Casting.asList(b.getExact(0)).length();

		if (a.getExact(0).isa(Obj.DOUBLELIST) && b.getExact(0).isa(Obj.DOUBLELIST)) {
			return fastInnerLoop(m, p, q, a, b);
		} else {
			return innerLoop(m, p, q, a, b);
		}
	}
	
	private static List innerLoop(int m, int p, int q, List a, List b) {
		// C is (m x q)
		ArrayList<NumberList> c = newEmpty(m, q);
		
		// Store b_k casting operations so we don't have to do it in the innermost loop
		ArrayList<NumberList> b_k = new ArrayList<NumberList>();
		for (int i = 0; i < p; i++) {
			b_k.add(Casting.asList(b.getExact(i)).toNumberList());
		}
		
		for (int i = 0; i < m; i++) {
			NumberList a_i = Casting.asList(a.getExact(i)).toNumberList();
			for (int j = 0; j < q; j++) {
				for (int k = 0; k < p; k++) {
					c.get(i).set(j, 
						NumberMath.add(c.get(i).get(j), NumberMath.mul(a_i.get(k), b_k.get(k).get(j)))
					);
				}
			}
		}
		
		// Convert to List
		List out = new List();
		for (NumberList x : c) out.mutAdd(new List(x));
		return out;
	}
	
	private static List fastInnerLoop(int m, int p, int q, List a, List b) {
		// C is (m x q)
		ArrayList<DoubleList> c = newEmptyDoubleList(m, q);
		
		// Store b_k casting operations so we don't have to do it in the innermost loop
		ArrayList<DoubleList> b_k = new ArrayList<DoubleList>();
		for (int i = 0; i < p; i++) {
			b_k.add( ((DoubleList)(Casting.asList(b.getExact(i)).toNumberList())) );
		}
		
		for (int i = 0; i < m; i++) {
			double[] a_i = ((DoubleList)(Casting.asList(a.getExact(i)).toNumberList())).internalArray();
			double[] c_i = c.get(i).internalArray();
			for (int j = 0; j < q; j++) {
				for (int k = 0; k < p; k++) {
					c_i[j] = c_i[j] + a_i[k] *  b_k.get(k).getDouble(j);
					//c.get(i).set(j, 
					//	NumberMath.add(c.get(i).get(j), NumberMath.mul(a_i.get(k), b_k.get(k).get(j)))
					//);
				}
			}
		}
		
		// Convert to List
		List out = new List();
		for (DoubleList x : c) out.mutAdd(new List(x));
		return out;
	}

}
