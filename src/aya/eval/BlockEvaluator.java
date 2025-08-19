package aya.eval;

import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

import aya.ReprStream;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.EmptyStackError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.util.Casting;

/** 
 * BlockEvaluator contain instructions and the resulting stacks 
 * @author Nick
 *
 */
public class BlockEvaluator {
	
	private ExecutionContext _context;
	protected Stack<Obj> stack;
	protected InstructionStack instructions;
	
	/** Create a new blockEvaluator with empty instructions and stack */
	protected BlockEvaluator(ExecutionContext context) {
		this._context = context;
		this.stack = new Stack<Obj>();
		this.instructions = new InstructionStack();
	}
	
	/** Create a new blockEvaluator with empty stack */
	protected BlockEvaluator(ExecutionContext context, InstructionStack il) {
		this._context = context;
		this.stack = new Stack<Obj>();
		this.instructions = il;
	}
	
	public ExecutionContext getContext() {
		return _context;
	}
	
	/** Returns the output stack */
	public Stack<Obj> getStack() {
		return this.stack;
	}
	
	/** Copy stack */
	public void addStack(BlockEvaluator other) {
		stack.addAll(other.getStack());
	}
	
	
	/** Pushes an object to the output stack */
	public void push(final Obj o) {
		stack.push(o);
	}

	public void pushBack(final Obj o) {
		stack.add(0, o);
	}

	/** pops from the output stack */
	public Obj pop() {
		return stack.pop();
	}
	
	/** Peeks into the output stack */
	public Obj peek() {
		return stack.peek();
	}

	/**
	 * Peeks into the output stack
	 * @param depth index into the stack, starting at 0
	 */
	public Obj peek(int depth) {
		int idx = stack.size() - (depth + 1);
		if (idx < 0 || idx >= stack.size())
			throw new EmptyStackError("Unexpected empty stack (expected depth of at least " + (depth + 1) + ")");
		return stack.get(idx);
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
	
	public void dump(StaticBlock block) {
		block.dumpToBlockEvaluator(this);
	}
	
	/** Evaluates each instruction in the instruction stack and places the result in the output stack */ 
	public void eval() {
		while (!instructions.isEmpty()) {
			Instruction instr = instructions.pop();
			
			try {
				instr.execute(this);
			} catch (EmptyStackException es) {
				EmptyStackError es2 = new EmptyStackError("Unexpected empty stack while executing instruction: " + instr);
				es2.setSource(instr.getSource());
				throw es2;
			//}// catch (NullPointerException npe) {
			//	throw npe;
			//	throw new RuntimeException(npe);
			} catch (AyaRuntimeException are) {
				are.setSource(instr.getSource());
				throw are;
			}
		}
	}


	/** Sets the stack */
	public void setStack(Stack<Obj> dupStack) {
		this.stack = dupStack;
	}

	
	
	
	/** Returns a string representation of the output stack */
	public String getPrintOutputState() {
		StringBuilder sb = new StringBuilder();
		try {
			for(Obj o : stack) {
				sb.append(o.repr(new ReprStream()) + " ");
			}
		} catch (Exception e) {
			throw new ValueError(e.getMessage() + "\n\tJust after\n" + sb.toString());
		}
		return sb.toString();
	}
	
	public boolean hasOutputState() {
		return !stack.isEmpty();
	}
	
	/** Returns a string representation of the output stack with type annotations */
	public String getOutputStateDebug() {
		StringBuilder sb = new StringBuilder();
		for(Obj o : stack) {
			sb.append(o.repr(new ReprStream()));
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/** Returns the instruction object for this blockEvaluator */
	public InstructionStack getInstructions() {
		return instructions;
	}
	

	/** Adds an item to the back of the instruction stack. (opposite of add()) */
	public void addBack(Obj b) {
		instructions.insert(0, new DataInstruction(b));
	}
	
	/** Adds a stack to this blockEvaluator. Reverses the stack before adding */
	public void appendToStack(Stack<Obj> stk) {
		Collections.reverse(stk);
		while (!stk.empty()) {
			this.stack.push(stk.pop());
		}
	}
	
	/** If the variable is a blockEvaluator, dump to the instructions
	 * else add the item to the stack
	 */
	public void addOrDumpVar(Obj o) {
		if (o.isa(Obj.BLOCK)) {
			this.dump(Casting.asStaticBlock(o));
		} else {
			stack.push(o);

		}

	}
	
	/** Calls the variable and dumps the result to the stack existing in the input blockEvaluator */
	public void callVariable(Dict dict, Symbol keyVar, Obj... push_first) {
		//Push self
		if (dict.pushSelf()) {
			stack.push(dict);
		}
		
		//push others
		for (Obj o : push_first) {
			stack.push(o);
		}
		
		Obj obj = dict.get(keyVar);
		
		if(obj.isa(Obj.BLOCK)) {
			StaticBlock blk = Casting.asStaticBlock(obj);
			this.dump(blk);
		} else {
			stack.push(obj);
		}
	}
	
}
