package aya.instruction;

import java.util.ArrayList;
import java.util.Stack;

import aya.ReprStream;
import aya.eval.ExecutionContext;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.list.List;
import aya.obj.list.ListIterationFunctions;
import aya.obj.list.ListRangeUtils;
import aya.parser.SourceStringRef;

public class ListBuilderInstruction extends Instruction {

	
	private StaticBlock initialList;
	private StaticBlock map;
	private StaticBlock[] filters;
	private int num_captures;

	public ListBuilderInstruction(SourceStringRef source, StaticBlock initial, StaticBlock map, StaticBlock[] filters, int num_captures) {
		super(source);
		this.initialList = initial;
		this.map = map;
		this.filters = filters;
		this.num_captures = num_captures;
	}
	
	public List createList(ExecutionContext context, Stack<Obj> outerStack) {
		BlockEvaluator evaluator = context.createEvaluator();
		evaluator.dump(initialList);

		for (int p = 0; p < num_captures; p++) {
			evaluator.add(outerStack.pop());
		}
		
		evaluator.eval();
		
		ArrayList<Obj> res = new ArrayList<Obj>();			//Initialize the argument list
		res.addAll(evaluator.getStack());					//Copy the results into the argument list
		
		boolean allLists = false;							//Check if all arguments are lists
		if(res.size() > 1) {
			allLists = true;								//All arguments may be a list
			for (Obj o : res) {
				if(!o.isa(Obj.LIST)) {
					allLists = false;
					break;
				}
			}
		}
		
		ArrayList<Obj> list = null;
		List outList = null;
		
		//If all arguments are lists, dump each list's respective element onto the stack of the map blockEvaluator
		// [[1 2][3 4], +] => 1 3 +, 2 4 + => [4 6]
		if (allLists) {
			ArrayList<List> listArgs = new ArrayList<List>(res.size());
			int size = -1;
			
			//Check lengths and cast objects
			for(int i = 0; i < res.size(); i++) {
				listArgs.add((List)(res.get(i)));
				if(size == -1) {
					size = listArgs.get(0).length();
				} else if (size != listArgs.get(i).length()) {
					throw new ValueError("List Builder: All lists must be same length");
				}
			}
			
			list = new ArrayList<Obj>(size);
			
			//Dump items from the lists into the blocks and apply the map if needed
			for(int i = 0; i < size; i++) {
				BlockEvaluator b = context.createEvaluator();
				for (int j = 0; j < listArgs.size(); j++) {
					b.push(listArgs.get(j).getExact(i));
				}
				
				//Apply the map
				if(map != null) {
					b.dump(map);
					b.eval();		
				}
				list.addAll(b.getStack());
			}
			
			outList = new List(list);
			
		} else {
			outList = new List(ListRangeUtils.buildRange(new List(res)));							//Create the initial range
			if(map != null) {
				outList = ListIterationFunctions.map(context, outList, this.map);
			}
		}
		
		if(filters != null) {								//Apply the filters to the list
			for (StaticBlock filter : filters) {
				outList = ListIterationFunctions.filter(context, outList, filter);
			}
		}
		return outList;
	}
	

	@Override
	public void execute(BlockEvaluator b) {
		b.push(createList(b.getContext(), b.getStack()));
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("[");
		if (num_captures > 0) {
			stream.print(num_captures);
			stream.print("| ");
		}

		BlockUtils.repr(stream, initialList, false);
		stream.print(", ");

		boolean has_body = false;
		if(map != null) {
			BlockUtils.repr(stream, map, false);
			stream.print(", ");
			has_body = true;
		}
		if(filters != null) {
			for (StaticBlock b : filters) {
				BlockUtils.repr(stream, b, false);
				stream.print(", ");
			}
			has_body = true;
		}
		if (has_body) stream.backspace(2);
		stream.print("]");
		return stream;
	}

	
}
