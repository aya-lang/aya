package aya.ext.thread;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import aya.AyaThread;
import aya.ExecutionRequest;
import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.list.List;
import aya.util.Casting;

public class ThreadInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {

		addInstruction(new NamedOperator("thread.runall", "list files in working dir") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				ExecutionContext context = blockEvaluator.getContext();

				final Obj blocks_obj = blockEvaluator.pop();
				
				if (blocks_obj.isa(Obj.LIST)) {
					List blocks = Casting.asList(blocks_obj);
					
					ArrayList<StaticBlock> tasks = new ArrayList<StaticBlock>();
					for (int i = 0; i < blocks.length(); i++) {
						if (blocks.getExact(i).isa(Obj.BLOCK)) {
							tasks.add(Casting.asStaticBlock(blocks.getExact(i)));
						} else {
							throw new TypeError(this, "L<B>", blocks_obj);
						}
					}
					
					ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
					
					// Spawn a thread for each task
					for (int i = 0; i < tasks.size(); i++) {
						AyaThread thread = AyaThread.spawnThread(context.createChild());
						thread.queueInput(new ExecutionRequest(-1, tasks.get(i)));
						executor.execute(thread);
					}
					
					executor.shutdown();
					try {
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						executor.shutdownNow();
						throw new ValueError(e.getLocalizedMessage());
					}
				} else {
					throw new TypeError(this, "L<B>", blocks_obj);
				}
			}
		});
		
	}
}
