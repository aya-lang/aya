package aya;

import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.ValueError;

public class AyaThread extends Thread {
	
	private final BlockingQueue<ExecutionRequest> _input = new LinkedBlockingQueue<ExecutionRequest>();
	private final BlockingQueue<ExecutionResult> _output = new LinkedBlockingQueue<ExecutionResult>();
	private ExecutionContext _root = null;
	private ReentrantLock _lock = new ReentrantLock();
	private volatile boolean _running;
	

	protected AyaThread(ExecutionContext context) {
		_root = context;
	}
	
	public static AyaThread spawnThread(ExecutionContext context) {
		AyaThread thread = new AyaThread(context);
		return thread;
	}
	
	public boolean hasUnfinishedTasks() {
		return _lock.isLocked() || _input.size() > 0;
	}
	 
	@Override
	public void run() {
		_running = true;
		while (_running) {
			try {
				ExecutionRequest input = _input.take();

				_lock.lock();
			
				if (input == null) {
					StaticData.IO.out().println("Exiting...");
					break;
				}
				
				ExecutionResult result = eval(input);
				
				_output.add(result);

				_lock.unlock();

			} catch (InterruptedException e) {
				// Exit
			}
		}
	}
	
	public void queueInput(ExecutionRequest request) {
		_input.offer(request);
	}
	
	public ExecutionResult waitForResponse() throws InterruptedException {
		return _output.take();
	}
	
	public ExecutionResult eval(ExecutionRequest request) {
		ExecutionResult result;
		BlockEvaluator b = _root.createEvaluator();
		b.dump(request.getBlock());
		try {
			b.eval();
			result = new ExecutionResultSuccess(request.id(), b.getStack());
		} catch (AyaRuntimeException ex) {
			result = new ExecutionResultException(request.id(), ex, _root.getCallStack());
		} catch (Exception e) {
			PrintStream err = StaticData.IO.err();
			err.println(DebugUtils.exToString(e));
			try {
				if (b.hasOutputState())
					err.println("stack:\n\t" + b.getPrintOutputState());
				if (b.getInstructions().size() > 0)
					err.println("just before:\n\t" + b.getInstructions().toString());
				if (!_root.getCallStack().isEmpty())
					err.print(_root.getCallStack().toString());
			} catch (Exception e2) {
				err.println("An additional error was thrown when attempting to print the stack state:");
				err.println(DebugUtils.exToString(e2));
				err.println("This is likely caused by an error in an overloaded __str__ or __repr__ blockEvaluator.");
			} 
			result = new ExecutionResultException(request.id(), new ValueError("TODO"), _root.getCallStack());
		} finally {
			_root.getVars().reset();
			_root.getCallStack().reset();
		}
		
		return result;
	}

}


