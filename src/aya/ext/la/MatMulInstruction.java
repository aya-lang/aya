package aya.ext.la;

import java.util.ArrayList;

import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.NumberMath;
import aya.util.Casting;

public class MatMulInstruction extends NamedInstruction {
	
	public MatMulInstruction() {
		super("la.mul");
		_doc = ("2d matrix multiplication");
	}

	@Override
	public void execute(Block block) {
		Obj b = block.pop();
		Obj a = block.pop();

		if (a.isa(Obj.LIST) && b.isa(Obj.LIST)) {
			block.push(matMulSafe(Casting.asList(a), Casting.asList(b)));
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

	private static List matMul(List a, List b) {
		// A is (m x n)
		int m = a.length();
		//int n = Casting.asList(a.getExact(0)).length();
		// B in (p x q)
		int p = b.length();
		int q = Casting.asList(b.getExact(0)).length();
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

}
