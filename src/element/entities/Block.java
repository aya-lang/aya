package element.entities;

import static element.ElemTypes.isBlock;
import static element.ElemTypes.isFlag;
import static element.ElemTypes.isLambda;
import static element.ElemTypes.isListBuilder;
import static element.ElemTypes.isListLiteral;
import static element.ElemTypes.isOp;
import static element.ElemTypes.isTuple;
import static element.ElemTypes.isUserObject;
import static element.ElemTypes.isVar;
import static element.ElemTypes.isVarSet;
import static element.ElemTypes.toBlock;
import static element.ElemTypes.toFlagID;
import static element.ElemTypes.toLambda;
import static element.ElemTypes.toListBuider;
import static element.ElemTypes.toListLiteral;
import static element.ElemTypes.toOp;
import static element.ElemTypes.toTuple;
import static element.ElemTypes.toUserObject;
import static element.ElemTypes.toVar;
import static element.ElemTypes.toVarSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

import element.ElemTypes;
import element.Element;
import element.exceptions.ElementRuntimeException;
import element.variable.MemberVariable;
import element.variable.Module;
import element.variable.ModuleFactory;
import element.variable.Variable;
import element.variable.VariableSet;

public class Block {
	protected Stack<Object> stack;
	protected InstructionStack instructions;
	//private boolean listLiteral = false;
	
	public Block() {
		this.stack = new Stack<Object>();
		this.instructions = new InstructionStack();
	}
	
	public Block(InstructionStack il) {
		this.stack = new Stack<Object>();
		this.instructions = il;
	}
	
	/** Returns the output stack */
	public Stack<Object> getStack() {
		return this.stack;
	}
	
	/** Pushes an object to the output stack */
	public void push(final Object o) {
		stack.push(o);
	}

	/** pops from the output stack */
	public Object pop() {
		return stack.pop();
	}
	
	/** Peeks into the output stack */
	public Object peek() {
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
			if (isOp(current)) {
				try {
					toOp(current).execute(this);  
				} catch (EmptyStackException es) {
					throw new ElementRuntimeException("Empty stack at operator '" + toOp(current).name + "'");
				}
			}
			
			// Variable Set: Push it to Element's variable data
			else if (isVarSet(current)) {
				VariableSet vars = toVarSet(current).clone();
				vars.setArgs(this);
				Element.getInstance().getVars().add(vars);
			}
			
			//Variable: Decide weather to read or write
			else if (isVar(current)) {
				Variable var = toVar(current);
				if(var.shouldBind()) {
					Element.getInstance().getVars().setVar(var, stack.peek());
				} else {
					Object o = Element.getInstance().getVars().getVar(toVar(current));
					if (isBlock(o)) {
						instructions.addAll(toBlock(o).getInstructions().getInstrucionList());
					} else {
						stack.push(o);
					}
				}
			}
			
			else if (current instanceof ModuleFactory) {
				Module mod = ((ModuleFactory)current).getModule();
				Element.getInstance().getVars().setVar(mod.id, mod);
			}
			
			//Member Variable
			else if (current instanceof MemberVariable) {
				Object m = null;
				try {
					m = stack.pop();
				} catch (EmptyStackException e) {
					throw new ElementRuntimeException("Expected module name before " + ((MemberVariable)current).toString());
				}
				
				if (m instanceof Module) {
					Object o = ((Module)m).get((MemberVariable)current);
					if (isBlock(o)) {
						instructions.addAll(toBlock(o).getInstructions().getInstrucionList());
					} else {
						stack.push(o);
					}
				}
//				//Of the form [module [data]] .var -> [module [data]] (deref module.var)
//				else if (m instanceof ArrayList) {
//					ArrayList<Object> l = (ArrayList<Object>)m;
//					if (l.size() == 2 && l.get(0) instanceof Module && l.get(1) instanceof ArrayList) {
//						stack.push(m); //Leave the obj on the stack
//						Object o = ((Module)l.get(0)).get((MemberVariable)current);
//						if (isBlock(o)) {
//							instructions.addAll(toBlock(o).getInstructions().getInstrucionList());
//						} else {
//							stack.push(o);
//						}
//					} else {
//						throw new ElementRuntimeException("Invalid struct: " + show(m));
//					}
//				} else {
//					throw new ElementRuntimeException("No module found before " + toMemVar(current).toString());
//				}
				
				//The first argument is a user object
				else if (isUserObject(m)) {
					stack.push(m); //Leave the obj on the stack
					UserObject user_obj = toUserObject(m);
					Object o = user_obj.getModule().get((MemberVariable)current);
					if (isBlock(o)) {
						instructions.addAll(toBlock(o).getInstructions().getInstrucionList());
					} else {
						stack.push(o);
					}
				} 
				
				//The second argument is a user object
				else if(isUserObject(stack.peek())) {
					//Leave the second arg on the stack
					UserObject user_obj = toUserObject(stack.peek());
					//Put the first back onto the stack
					stack.push(m);
					
					//Call the member variable
					Object o = user_obj.getModule().get((MemberVariable)current);
					if (isBlock(o)) {
						instructions.addAll(toBlock(o).getInstructions().getInstrucionList());
					} else {
						stack.push(o);
					}
					
				}
			}
			
			//Flag: Special instructions for the interpreter
			//Negative valued flags are ticks (`)
			else if(isFlag(current)) {
				switch(toFlagID(current)) {
				//Pop a variable set from the current variable data
				//This happens when we exit the scope of a function, etc.
				case Flag.POPVAR:
					Element.getInstance().getVars().pop();
					break;
				case Flag.EVAL_BLOCK:
					if(isBlock(this.peek())) {
						this.addBlock(toBlock(this.pop()));
					} else {
						System.out.println("Could not add block");
					}
					break;
				default:
					//Tick operator
					if (toFlagID(current) < 0) {
						try {
							instructions.holdNext(-1*toFlagID(current));
						} catch (IndexOutOfBoundsException e) {
							throw new ElementRuntimeException("Tick Operator: Error attempting to move object back " + (-1*toFlagID(current)) + " instructions");
						}
					} else {
						System.out.println("Unknown flag");
					}
				}
			}
			
			else if (current instanceof InterpolateString) {
				stack.push( ((InterpolateString) current).evalString());
			}
			
			//Lambda: Execute the block
			else if (isLambda(current)){
				instructions.addAll(toLambda(current).getInstructions().getInstrucionList());
			}
			
			//Tuple: Execute the statements
			else if (isTuple(current)) {
				stack.addAll(toTuple(current).evalToResults());
			}
			
			//ListBuilder: Build the list
			else if (isListBuilder(current)) {
				stack.push(toListBuider(current).createList(stack));
			}
			
			else if (isListLiteral(current)) {				
				stack.push(toListLiteral(current).getListCopy(stack));
			}
			
			//Block: If it is a list literal, execute it and wrap it in a list
			else if (isBlock(current)) {
//				if(toBlock(current).isListLiteral()) {
//					//Block b = toBlock(current);
//					Block b = toBlock(current).duplicate();
//					b.eval();
//					stack.push(new ArrayList<Object>(b.getStack()));
//				} else {
					push(current);
//				}
			}
			else {
				//Literal
				this.push(current);
			}
		}
		return;
	}
	
	/** Creates a duplicate of a block without interfering with the block */
	public Block duplicate() {
		Block out = new Block(this.instructions.duplicate());
		out.stack.addAll(this.stack);
//		Stack<Object> dupStack = new Stack<Object>();
//		dupStack.addAll(this.stack);
//		out.setStack(dupStack);
		return out;
	}

	/** Sets the stack */
	public void setStack(Stack<Object> dupStack) {
		this.stack = dupStack;
	}

	/** Maps a block to a list and returns the new list. The block is not effected */
	public ArrayList<Object> mapTo(ArrayList<Object> list) {
		ArrayList<Object> out = new ArrayList<Object>(list.size());
		Block b = new Block();
		for (Object o : list) {
			b.addAll(this.instructions.getInstrucionList());
			b.add(o);
			b.eval();
			out.add(b.pop());
			b.clear();
		}
		return out;
	}
	
	/** Applies this block as a filter to a list */
	public ArrayList<Object> filter(ArrayList<Object> list) {
		ArrayList<Object> out = new ArrayList<Object>();
		Block b = new Block();
		for (Object o : list) {
			b.addAll(this.instructions.getInstrucionList());
			b.add(o);
			b.eval();
			if(ElemTypes.isBool(b.peek())) {
				if(ElemTypes.toBool(b.peek())) {
					out.add(o);
				}
			} else {
				throw new ElementRuntimeException("ListBuilder, filter: item must be a bool. Recieved: " + ElemTypes.debugString(b.peek()));
			}
			b.clear();
		}
		return out;
	}
	
	/** Returns a string representation of the output stack */
	public String getPrintOutputState() {
		StringBuilder sb = new StringBuilder();
		for(Object o : stack) {
			sb.append(ElemTypes.show(o) + " ");
		}
		return sb.toString();
	}
	
	/** Returns a string representation of the output stack with type annotations */
	public String getOutputStateDebug() {
		StringBuilder sb = new StringBuilder();
		for(Object o : stack) {
			sb.append(ElemTypes.debugString(o));
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
	
	public String toString() {
//		if (listLiteral) {
//			return "["+instructions+"]";
//		}
		return "{"+instructions+"}";
	}

	/** Adds an item to the front of the instruction stack. (opposite of add()) */
	public void addToFontInstructions(Object b) {
		instructions.insert(0, b);
		
	}
	
	/** Adds a stack to this block. Reverses the stack before adding */
	public void appendToStack(Stack<Object> stk) {
		Collections.reverse(stk);
		while (!stk.empty()) {
			this.stack.push(stk.pop());
		}
	}

}
