package aya.obj.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

import aya.entities.InstructionStack;
import aya.exceptions.AyaRuntimeException;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.LambdaInstruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;

/** 
 * Block contain instructions and the resulting stacks 
 * @author Nick
 *
 */
public class Block extends Obj {
	
	protected Stack<Obj> stack;
	protected InstructionStack instructions;
	
	/** Create a new block with empty instructions and stack */
	public Block() {
		this.stack = new Stack<Obj>();
		this.instructions = new InstructionStack();
	}
	
	/** Create a new block with empty stack */
	public Block(InstructionStack il) {
		this.stack = new Stack<Obj>();
		this.instructions = il;
	}
	
	/** Returns the output stack */
	public Stack<Obj> getStack() {
		return this.stack;
	}
	
	/** Pushes an object to the output stack */
	public void push(final Obj o) {
		stack.push(o);
	}

	/** pops from the output stack */
	public Obj pop() {
		return stack.pop();
	}
	
	/** Peeks into the output stack */
	public Obj peek() {
		return stack.peek();
	}
	
	/** Pops the next instruction from the instruction list */
	public Instruction next() {
		return instructions.pop();
	}
	
	/** Clears the stack only */
	public void clearStack() {
		stack.clear();
	}
	
	/** Clears the stack and the instructions */
	public void clear() {
		stack.clear();
		instructions.clear();
	}
	
	/** Add an object to the instruction stack */
	public void add(Instruction o) {
		instructions.push(o);
	}
	
	/** Add data to the instruction stack */
	public void add(Obj data) {
		add(new DataInstruction(data));
	}
	
	/** Adds an instruction to a specified location on the instruction stack */
	public void add(int i, Instruction o) {
		instructions.insert(i, o);
	}
	
	/** Adds data to a specified location on the instruction stack */
	public void add(int i, Obj o) {
		add(i, new DataInstruction(o));
	}
	
	/** Adds a collection of objects to the instruction stack */
	public void addAll(Collection<? extends Instruction> list) {
		instructions.addAll(list);
	}
	
	/** Returns true if there are no more instructions in the instruction stack */
	public boolean isEmpty() {
		return instructions.isEmpty();
	}
	
	/** Returns true if there are no items remaining in the stack */
	public boolean stackEmpty() {
		return stack.isEmpty();
	}
	
	/** Test if this block has a local variable set */
	public boolean hasLocals() {
		if (instructions.isEmpty()) return false;
		final Instruction flag = instructions.getInstrucionList().get(0);
		return flag instanceof PopVarFlagInstruction;
	}
	
	/** Get a list of args for this block */
	public ArrayList<Symbol> getArgs() {
		ArrayList<Symbol> list = new ArrayList<>();
		return list;
	}
	
	/** Evaluates each instruction in the instruction stack and places the result in the output stack */ 
	public void eval() {
		while (!instructions.isEmpty()) {
			Instruction instr = instructions.pop();
			
			try {
				instr.execute(this);
			} catch (EmptyStackException es) {
				throw new AyaRuntimeException("Unexpected empty stack while executing instruction: " + instr);
			} catch (NullPointerException npe) {
				throw new RuntimeException(npe.toString());
			}
		}
	}
	
	/** Creates a duplicate of a block without interfering with the block */
	public Block duplicate() {
		Block out = new Block(this.instructions.duplicate());
		out.stack.addAll(this.stack);
		return out;
	}

	/** Sets the stack */
	public void setStack(Stack<Obj> dupStack) {
		this.stack = dupStack;
	}

	/** Maps a block to a list and returns the new list. The block is not effected */
	public List mapTo(List list) {
		ArrayList<Obj> out = new ArrayList<Obj>(list.length());
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(this.instructions.getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			out.addAll(b.stack);
			b.clear();
		}
		return new GenericList(out).promote();
	}
	
	/** Maps a block to a list and returns the new list. The block is not effected */
	public List mapToPushStack(Obj obj, List list) {
		ArrayList<Obj> out = new ArrayList<Obj>(list.length());
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.push(obj.deepcopy());
			b.addAll(this.instructions.getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			out.addAll(b.stack);
			b.clear();
		}
		return new GenericList(out).promote();
	}
	
	
	
	/** Maps a block to a dictionary and returns nothing. Neither the block nor the dict is effected */
	public void mapTo(Dict dict) {
		Block b = new Block();
		ArrayList<Long> keys = dict.keys();
		for (long key : keys) {
			b.addAll(this.instructions.getInstrucionList());
			b.push(Symbol.fromID(key));
			b.push(dict.get(key));
			b.eval();
			b.clear();
		}
	}
	
	/** Applies this block as a filter to a list */
	public List filter(List list) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(this.instructions.getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			if(b.peek().bool()) {
				out.add(list.get(i));
			}
			b.clear();
		}
		return new GenericList(out).promote();
	}
	
	/** Like filter but returns a list of true/false values representing
	 * the outcome of each applying the block to each item in the list
	 * @param list
	 * @return
	 */
	public boolean[] truthIdxs(List list) {
		boolean[] out = new boolean[list.length()];
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(this.instructions.getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			out[i] = b.peek().bool();
			b.clear();
		}
		return out;
	}
	
	/** Returns a string representation of the output stack */
	public String getPrintOutputState() {
		StringBuilder sb = new StringBuilder();
		for(Obj o : stack) {
			sb.append(o.repr() + " ");
		}
		return sb.toString();
	}
	
	/** Returns a string representation of the output stack with type annotations */
	public String getOutputStateDebug() {
		StringBuilder sb = new StringBuilder();
		for(Obj o : stack) {
			sb.append(o.repr());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/** Returns the instruction object for this block */
	public InstructionStack getInstructions() {
		return instructions;
	}
	
	/** Adds a block to this block (does not duplicate the block) */
	public void addBlock(Block b) {
		if (b.hasLocals()) {
			this.instructions.push(new LambdaInstruction(b.getInstructions()));
		} else {
			this.instructions.addAll(b.getInstructions().getInstrucionList());
		}
	}
	
	public void addBlockBack(Block b) {
		if (b.hasLocals()) {
			this.instructions.insert(0, new LambdaInstruction(b.getInstructions()));
		} else {
			this.instructions.addAll(0, b.getInstructions().getInstrucionList());
		}
	}
	


	/** Adds an item to the back of the instruction stack. (opposite of add()) */
	public void addBack(Obj b) {
		instructions.insert(0, new DataInstruction(b));
		
	}
	
	/** Adds a stack to this block. Reverses the stack before adding */
	public void appendToStack(Stack<Obj> stk) {
		Collections.reverse(stk);
		while (!stk.empty()) {
			this.stack.push(stk.pop());
		}
	}
	
	/** If the variable is a block, dump to the instructions
	 * else add the item to the stack
	 */
	public void addOrDumpVar(Obj o) {
		if (o.isa(Obj.BLOCK)) {
			instructions.addAll(((Block)o).getInstructions().getInstrucionList());
		} else {
			stack.push(o);

		}

	}
	
	/** Calls the variable and dumps the result to the stack existing in the input block */
	public void callVariable(Dict dict, Variable keyVar, Obj... push_first) {
		//Push self
		stack.push(dict);
		
		//push others
		for (Obj o : push_first) {
			stack.push(o);
		}
		
		Obj obj = dict.get(keyVar);
		
		if(obj.isa(Obj.BLOCK)) {
			Block blk = ((Block)obj).duplicate();
			instructions.addAll(blk.getInstructions().getInstrucionList());
		} else {
			stack.push(obj);
		}
	}
	
	/** If true, return "{instructions}" else just "instructions" */
	public String toString(boolean printBraces) {
		if (printBraces) {
			return "{" + instructions.toString() + "}";
		} else {
			return instructions.toString();
		}
	}
	
	public String toString() {
		return this.toString(true);
	}
	
	
	

	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	
	@Override
	public Obj deepcopy() {
		return this.duplicate();
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public String repr() {
		return this.toString(true);
	}

	@Override
	public String str() {
		return this.toString(true);
	}

	@Override
	public boolean equiv(Obj o) {
		// Always return false
		return false;
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.BLOCK;
	}

	@Override
	public byte type() {
		return Obj.BLOCK;
	}

	

	

}
