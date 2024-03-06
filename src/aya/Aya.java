package aya;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aya.eval.ExecutionContext;

public class Aya extends Thread {
	
	private final BlockingQueue<ExecutionRequest> _input = new LinkedBlockingQueue<ExecutionRequest>();
	private ExecutionContext _root = null;
	
	
	protected Aya(ExecutionContext context) {
		_root = context;
	}
	
	public static Aya spawnThread(ExecutionContext context) {
		Aya thread = new Aya(context);
		return thread;
	}
	
	
	@Override
	public void run() {
		while (true) {
			try {
				ExecutionRequest input = _input.take();
				
				synchronized(this) {
					if (input == null) {
						StaticData.IO.out().println("Exiting...");
						break;
					}
					
					//_instance.run(new SourceString(input, "<interactive>"));
					_root.run(input.getBlock());

					if (_input.isEmpty()) {
						notify();
					}
				}
				
			} catch (InterruptedException e) {
				System.err.println("Aya interupted: " + e);
			}
		}
	}
	
	public void queueInput(ExecutionRequest request) {
		_input.offer(request);
	}
	
	public void quit() {
		queueInput(null);
	}

}


