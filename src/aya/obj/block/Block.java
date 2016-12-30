package aya.obj.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

import aya.Aya;
import aya.entities.Flag;
import aya.entities.InstructionStack;
import aya.entities.InterpolateString;
import aya.entities.Lambda;
import aya.entities.ListBuilder;
import aya.entities.ListLiteral;
import aya.entities.Operation;
import aya.entities.Tuple;
import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.dict.DictFactory;
import aya.obj.dict.KeyVariable;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.variable.Variable;
import aya.variable.VariableSet;

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
	public Object next() {
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
	public void add(Object o) {
		instructions.push(o);
	}
	
	/** Adds an instruction to a specified location on the stack */
	public void add(int i, Object o) {
		instructions.insert(i, o);
	}
	
	/** Adds a collection of objects to the instruction stack */
	public void addAll(Collection<? extends Object> list) {
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
	
	/** Evaluates each instruction in the instruction stack and places the result in the output stack */ 
	public void eval() {
		while (!instructions.isEmpty()) {
			Object current = instructions.pop();
			
			//Operator: execute the operator on the block
			if (current instanceof Operation) {
				try {
					((Operation)current).execute(this);  
				} catch (EmptyStackException es) {
					throw new AyaRuntimeException("Empty stack at operator '" + ((Operation)current).name + "'");
				}
			}
			
			// Variable Set: Push it to Aya's variable data
			else if (current instanceof VariableSet) {
				VariableSet vars = ((VariableSet)current).clone();
				vars.setArgs(this);
				Aya.getInstance().getVars().add(vars);
			}
			
			// KeyVariable
			else if (current instanceof KeyVariable) {
				KeyVariable var = ((KeyVariable)current);
				Obj dict_obj = stack.pop();
				Dict dict;
				if (dict_obj.isa(Obj.DICT)) {
					dict = (Dict)dict_obj;
				} else {
					throw new AyaRuntimeException("Expected dict before key " + var.toString()
							+ ", recieved " + dict_obj.str()); 
				}
				
				if (var.shouldBind()) {
					dict.set(var, stack.pop());
					stack.push(dict);
				} else {
					Obj o = dict.get(var);
					if (o.isa(Obj.BLOCK)) {
						// If user object, leave it as the first item on the stack
						if (dict.hasMetaTable()) {
							stack.push(dict);
						}
						instructions.addAll( ((Block)o).getInstructions().getInstrucionList() );
					} else {
						stack.push(o);
					}
				}
			}
			
			//Variable: Decide weather to read or write
			else if (current instanceof Variable) {
				Variable var = ((Variable)current);
				if(var.shouldBind()) {
					Aya.getInstance().getVars().setVar(var, stack.peek());
				} else {
					Obj o = Aya.getInstance().getVars().getVar(((Variable)current));
					if (o.isa(Obj.BLOCK)) {
						instructions.addAll(((Block)o).getInstructions().getInstrucionList());
					} else {
						stack.push(o);
					}
				}
			}
			
			else if (current instanceof DictFactory) {
				stack.push(((DictFactory)current).getDict());
			}
			
			//Flag: Special instructions for the interpreter
			//Negative valued flags are ticks (`)
			else if(current instanceof Flag) {
				byte flagID = ((Flag)current).getID();
				switch(flagID) {
				//Pop a variable set from the current variable data
				//This happens when we exit the scope of a function, etc.
				case Flag.POPVAR:
					Aya.getInstance().getVars().pop();
					break;
				case Flag.EVAL_BLOCK:
					if(this.peek().isa(Obj.BLOCK)) {
						this.addBlock((Block)(this.pop()));
					} else {
						System.out.println("Could not add block");
					}
					break;
				default:
					//Tick operator
					if (flagID < 0) {
						try {
							instructions.holdNext(-1*flagID);
						} catch (IndexOutOfBoundsException e) {
							throw new AyaRuntimeException("Tick Operator: Error attempting to move object back " + (-1*flagID) + " instructions");
						}
					} else {
						System.out.println("Unknown flag");
					}
				}
			}
			
			else if (current instanceof InterpolateString) {
				stack.push(new Str(((InterpolateString) current).evalString()));
			}
			
			//Lambda: Execute the block
			else if (current instanceof Lambda){
				instructions.addAll(((Lambda)current).getInstructions().getInstrucionList());
			}
			
			//Tuple: Execute the statements
			else if (current instanceof Tuple) {
				stack.addAll(((Tuple)current).evalToResults());
			}
			
			//ListBuilder: Build the list
			else if (current instanceof ListBuilder) {
				stack.push(((ListBuilder)current).createList(stack));
			}
			
			//ListFactory
			else if (current instanceof ListLiteral) {				
				stack.push(((ListLiteral)current).getListCopy(stack));
			}
			
			//Literal
			else {
				this.push((Obj)current);
			}
		}
		return;
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
			b.add(list.get(i));
			b.eval();
			out.addAll(b.stack);
			b.clear();
		}
		return new GenericList(out).promote();
	}
	
	/** Applies this block as a filter to a list */
	public List filter(List list) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(this.instructions.getInstrucionList());
			b.add(list.get(i));
			b.eval();
			if(b.peek().bool()) {
				out.add(list.get(i));
			}
			b.clear();
		}
		return new GenericList(out).promote();
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
		this.instructions.addAll(b.getInstructions().getInstrucionList());
	}
	


	/** Adds an item to the front of the instruction stack. (opposite of add()) */
	public void addToFontInstructions(Obj b) {
		instructions.insert(0, b);
		
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
			instructions.addAll( ((Block)o).getInstructions().getInstrucionList() );
		} else {
			stack.push(o);
		}
	}
	
	/** Calls the variable and dumps the result to the stack existing in the input block */
	public void callVariable(Dict dict, KeyVariable keyVar, Obj... push_first) {
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
