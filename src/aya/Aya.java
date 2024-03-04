package aya;

import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aya.eval.ExecutionContext;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;

public class Aya extends Thread {
	
	private final BlockingQueue<ExecutionRequest> _input = new LinkedBlockingQueue<ExecutionRequest>();
	private static Aya _instance = getInstance();
	private ExecutionContext _root = null;
	
	
	protected Aya() { }
	
	
	public static Aya getInstance() {
		if(_instance == null) {
			_instance = new Aya();
			_instance._root = ExecutionContext.createRoot(StaticData.IO);
			// Init the static data
			StaticData.getInstance().init();
			// Init global vars
			_instance._root.getVars().initGlobals(_instance._root);
			AyaPrefs.init();
		}
		return _instance;
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
	
	//Returns true if load was successful
	public boolean loadAyarc() {
		//Load the standard library
		try {
			String pathString = Paths.get(AyaPrefs.getAyaDir(), StaticData.ayarcPath).toString().replace("\\", "\\\\");
			StaticBlock blk = Parser.compileSafeOrNull(new SourceString("\"" + pathString + "\":F", "<ayarc loader>"), StaticData.IO);
			if (blk != null) getInstance().queueInput(new ExecutionRequest(blk));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public void quit() {
		queueInput(null);
	}

}


