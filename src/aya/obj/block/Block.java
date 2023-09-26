package aya.obj.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

import aya.ReprStream;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.EmptyStackError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.LambdaInstruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;

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
	
	/** Copy stack */
	public void addStack(Block other) {
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
	
	/** Get the block's header, return null if it does not have one */
	public BlockHeader getHeader() {
		if (instructions.size() > 0) {
			Instruction i = instructions.peek(0);
			if (i instanceof BlockHeader) {
				return (BlockHeader)i;
			}
		}
		return null;
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
		BlockHeader header = getHeader();
		if (header != null) {
			for (BlockHeaderArg arg : header.getArgs()) {
				list.add(arg.var);
			}
		}
		return list;
	}
	
	/** Evaluates each instruction in the instruction stack and places the result in the output stack */ 
	public void eval() {
		while (!instructions.isEmpty()) {
			Instruction instr = instructions.pop();
			
			try {
				instr.execute(this);
			} catch (EmptyStackException es) {
				EmptyStackError es2 = new EmptyStackError("Unexpected empty stack while executing instruction: " + instr);
				es2.addContext(instr, this);
				throw es2;
			} catch (NullPointerException npe) {
				throw new RuntimeException(npe);
			} catch (AyaRuntimeException are) {
				are.addContext(instr, this);
				throw are;
			}
		}
	}
	
	/** Creates a duplicate of a block without interfering with the block */
	public Block duplicate() {
		Block out = new Block(this.instructions.duplicate());
		out.stack.addAll(this.stack);
		return out;
	}
	
	/** Create a new block with the given header */
	public Block duplicateNewHeader(BlockHeader header) {
		if (getHeader() == null) {
			Block dup = duplicate();
			dup._addHeader(header);
			return dup;
		} else {
			Block dup = duplicateNoHeader();
			dup._addHeader(header);
			return dup;
		}
	}
	
	/** Assumes this block does not already have a header! */
	private void _addHeader(BlockHeader bh) {
		add(bh);
		add(0, PopVarFlagInstruction.INSTANCE);
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
	
	/** Returns the instruction object for this block */
	public InstructionStack getInstructions() {
		return instructions;
	}
	
	/** Adds a block to this block (does not duplicate the block) */
	public void addBlock(Block b) {
		if (b.hasLocals()) {
			this.instructions.push(new LambdaInstruction(null, b.getInstructions()));
		} else {
			this.instructions.addAll(b.getInstructions().getInstrucionList());
		}
	}
	
	public void addBlockBack(Block b) {
		if (b.hasLocals()) {
			this.instructions.insert(0, new LambdaInstruction(null, b.getInstructions()));
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
			Block blk = ((Block)obj).duplicate();
			instructions.addAll(blk.getInstructions().getInstrucionList());
		} else {
			stack.push(obj);
		}
	}
	


	
	/** Introspection: get all asguments and types from header (if exists) */
	public ArrayList<BlockHeaderArg> getArgsAndTypes() {
		BlockHeader header = getHeader();
		if (header != null) {
			return header.getArgs();
		} else {
			return new ArrayList<BlockHeaderArg>();
		}
	}

	/** Return a list of instructions not including the block header or pop var instruction */
	public Block duplicateNoHeader() {
		Block b = duplicate();
		ArrayList<Instruction> instructions = b.getInstructions().getInstrucionList();
		// Remove block header
		int len = instructions.size();
		if (len > 0) {
			int last = len-1;
			Instruction i = instructions.get(last);
			if (i instanceof BlockHeader) {
				instructions.remove(last);

				// There was a header, remove popvar flag instruction
				len = instructions.size();
				if (len > 0) {
					i = instructions.get(0);
					if (i instanceof PopVarFlagInstruction) {
						instructions.remove(0);
					} else {
						throw new RuntimeException("Expected popvar instruction in duplicateNoHeader");
					}
				}
			}
		}
		
		return b;
	}

	/** Return a copy of the block. If the original does not have a block header with local variables
	 * create an empty local variables in the copy
	 */
	public Block duplicateAddLocals() {
		 Block b = duplicate();
		 BlockHeader bh = b.getHeader();
		 if (bh == null) {
			 bh = new BlockHeader(null);
			 b.add(bh);
			 b.add(0, PopVarFlagInstruction.INSTANCE);
		 }
		 return b;
	}
	
	/** Split a block into a list of blocks, 1 per instruction */
	public List split() {
		ArrayList<Obj> blocks = new ArrayList<Obj>();
		ArrayList<Instruction> instructions = duplicateNoHeader().instructions.getInstrucionList();
		for (Instruction instr : instructions)
		{
			Block b = new Block();
			b.add(instr);
			blocks.add(0, b);
		}
		return new List(blocks);
	}
	
	/** Allow access to modify the block's local variables directly
	 *  If there are no locals, return null
	 * @return
	 */
	public Dict getLocals() {
		BlockHeader header = getHeader();
		if (header != null) {
			return header.getVars();
		} else {
			return null;
		}
	}

	
	///////////////////////
	// String Conversion //
	///////////////////////
	

	/** If true, return "{instructions}" else just "instructions" */
	private ReprStream blockRepr(ReprStream stream, boolean print_braces, HashMap<Symbol, Block> defaults) {
		if (print_braces) stream.print("{");
		instructions.repr(stream, defaults);
		if (print_braces) stream.print("}");
		return stream;
	}
	
	public String toString() {
		return blockRepr(new ReprStream(), true, null).toStringOneline();
	}


	public ReprStream repr(ReprStream stream, boolean print_braces) {
		return repr(stream, print_braces, null);
	}
	
	public ReprStream repr(ReprStream stream, boolean print_braces, HashMap<Symbol, Block> defaults) {
		if (stream.visit(this)) {
			blockRepr(stream, print_braces, defaults);
			stream.popVisited(this);
		} else {
			stream.print("{...}");
		}
		return stream;
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
	public ReprStream repr(ReprStream stream) {
		return repr(stream, true, null);
	}

	@Override
	public String str() {
		return repr(new ReprStream(), true).toStringOneline();
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
