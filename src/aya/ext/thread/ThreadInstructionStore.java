package aya.ext.thread;

import java.util.ArrayList;
import java.util.HashMap;

import aya.AyaThread;
import aya.ExecutionRequest;
import aya.ExecutionResult;
import aya.ExecutionResultUtils;
import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.runtime.ThreadError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.util.Casting;
import aya.util.Pair;

public class ThreadInstructionStore extends NamedInstructionStore {
	
	HashMap<Integer, AyaThread> _thread_table;
	volatile int _thread_counter;
	volatile int _request_id_counter;
	
	public ThreadInstructionStore() {
		_thread_table = new HashMap<Integer, AyaThread>();
		_thread_counter = 0;
	}
	
	private Pair<Integer, AyaThread> newThread(ExecutionContext context) {
		AyaThread thread = AyaThread.spawnChildThread(context);
		_thread_counter++;
		Pair<Integer, AyaThread> pair = new Pair<Integer, AyaThread>(_thread_counter, thread);
		_thread_table.put(pair.first(), pair.second());
		return pair;
	}
	
	// Throws an exception if the thread does not exist or is not alive
	private AyaThread getThread(int thread_id) {
		AyaThread thread = _thread_table.get(thread_id);
		if (thread == null) {
			throw new ValueError("Invalid thread id: " + thread_id);
		} else if (!thread.isAlive()) {
			throw new ThreadError(thread_id, "Thread is no longer alive");
		} else {
			return thread;
		}
	}
	
	private int newRequestId() {
		return _request_id_counter++;
	}
	
	@Override
	protected void init() {
		
		addInstruction(new NamedOperator("thread.new", "create a thread") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				Pair<Integer, AyaThread> thread = newThread(blockEvaluator.getContext());
				thread.second().start();
				blockEvaluator.push(Num.fromInt(thread.first()));
			}
		});
		
		addInstruction(new NamedOperator("thread.add_task", "add a task") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				Obj thread_id_obj = blockEvaluator.pop();
				Obj task_obj = blockEvaluator.pop();
				if (thread_id_obj.isa(Obj.NUM) && task_obj.isa(Obj.BLOCK)) {
					int thread_id = Casting.asNumber(thread_id_obj).toInt();
					AyaThread thread = getThread(thread_id);
					thread.queueInput(new ExecutionRequest(newRequestId(), Casting.asStaticBlock(task_obj)));
				} else {
					throw new TypeError(this, "N", thread_id_obj);
				}
			}
		});

		addInstruction(new NamedOperator("thread.wait_for_result", "wait for a result") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				Obj thread_id_obj = blockEvaluator.pop();
				if (thread_id_obj.isa(Obj.NUM)) {
					int thread_id = Casting.asNumber(thread_id_obj).toInt();
					AyaThread thread = getThread(thread_id);
					try {
						ExecutionResult res = thread.waitForResponse();
						List out = new List(ExecutionResultUtils.getDataOrThrowIfException(res));
						blockEvaluator.push(out);
					} catch (InterruptedException e) {
						throw new ThreadError(e);
					}
				} else {
					throw new TypeError(this, "N", thread_id_obj);
				}
			}
		});

		addInstruction(new NamedOperator("thread.has_unfinished_tasks", "check if the thread has any unfinished tasks") {
			@Override
			public void execute(BlockEvaluator blockEvaluator) {
				Obj thread_id_obj = blockEvaluator.pop();
				if (thread_id_obj.isa(Obj.NUM)) {
					int thread_id = Casting.asNumber(thread_id_obj).toInt();
					AyaThread thread = getThread(thread_id);
					blockEvaluator.push(Num.fromBool(thread.hasUnfinishedTasks()));
				} else {
					throw new TypeError(this, "N", thread_id_obj);
				}
			}
		});
		
		addInstruction(new NamedOperator("thread.runall", "Run all blocks in parallel") {
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
					
					ArrayList<AyaThread> threads = new ArrayList<AyaThread>();
					
					// Spawn a thread for each task
					for (int i = 0; i < tasks.size(); i++) {
						AyaThread thread = AyaThread.spawnChildThread(context);
						thread.queueInput(new ExecutionRequest(-1, tasks.get(i)));
						threads.add(thread);
					}
					
					for (AyaThread t : threads) t.start();
					
					List out = new List();

					for (AyaThread thread : threads) {
						ExecutionResult result;
						try {
							result = thread.waitForResponse();
						} catch (InterruptedException e) {
							throw new ThreadError(e);
						}
						
						ArrayList<Obj> data = ExecutionResultUtils.getDataOrThrowIfException(result);
						out.mutAdd(new List(data));
					}
					
					blockEvaluator.push(out);
					
				} else {
					throw new TypeError(this, "L<B>", blocks_obj);
				}
			}
		});
		
	}
}
