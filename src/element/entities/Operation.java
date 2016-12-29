package element.entities;

import element.obj.block.Block;

/**
 * The Operation Class
 * Every operator has some basic information (name, desc, argtypes)
 * and an execute method. The execute method is called by the interpreter
 * at run time and can manipulate a block
 * 
 * @author npaul
 *
 */
public abstract class Operation {
	public String info = "No info provided";
	public String name = "No name provided";
	public String argTypes = "A";
	public void execute(Block b){};	
	@Override public String toString() {return name;}
}
