# Multithreading

Aya has basic support for multithreading. Import thread functions using the `threading` module.

```
import ::threading
```

## `thread` object

Create a `thread` using `threading.new`.

```
aya> threading.new :my_thread
( 1 ) thread!
```

Add a task to a thread using `add_task` or `+` the task will be executed immediately. `add_task`/`+` return the thread. Examples below use `;` to discard so the thread is not printed in the REPL.

```
aya> aya> { "begin" :P 1 1 + "end" :P } my_thread.add_task ; .# OR my_thread +
begin
end
```

Get the result from a thread using `wait_for_result` or `.|`. The entire state of the stack is returned as a list.

```
aya> my_thread.wait_for_result .# OR my_thread .|
[ 2 ]
```

When multiple items left on stack during execution, all are returned as a list.

```
aya> { 1 2 /   3 4 + } my_thread.add_task ; .# OR my_thread +
aya> my_thread.wait_for_result
[ .5 7 ]
```


Attempting to call `wait_for_result`/`.|` on a thread with no tasks will throw an error

```
aya> threading.new :t
( 1 ) thread!
aya> t.wait_for_result
(thread 1) No tasks


> File '/home/npaul/git/aya/std/threading.aya', line 16, col 37:
15 | def thread::wait_for_result {self,
16 |     self.id :{thread.wait_for_result}
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~^
17 | }

Function call traceback:
  File '<input>', line 1 in .wait_for_result:
    t.wait_for_result
    ~~~~~~~~~~~~~~~~^
```

## Error Handling

If an error occurs while executing a task, the error will not be thrown until the result is accessed.

```
aya> .# Attempt to access index 99 of any empty list
aya> { "begin" :P   [ ].[99]   "end" :P } my_thread + ;   .# OR my_thread.add_task
begin
```

Notice that only "begin" has been printed. Execution failed before "end" was printed but no exception has been thrown in the main thread.

Attempting to access the result will throw the error.

```
aya> my_thread.wait_for_result .# OR my_thread .|
Invalid index 99 for list [ ]


{ "begin" :P   [ ].[99]   "end" :P } my_thread.add_task ;
~~~~~~~~~~~~~~~~~~^
Function call traceback:
  File '<input>', line 1 in .wait_for_result:
    my_thread.wait_for_result
    ~~~~~~~~~~~~~~~~~~~~~~~~^

```


## Parellel Execution


A thread may execute multiple tasks. These tasks are **not** executed in parellel. Tasks are always executed in the order they are received.

```
aya> { 1 } my_thread.add_task ;
aya> { 2 } my_thread.add_task ;
aya> { 3 } my_thread.add_task ;
aya> my_thread.wait_for_result
[ 1 ]
aya> my_thread.wait_for_result
[ 2 ]
aya> my_thread.wait_for_result
[ 3 ]
```

To execute multiple tasks in parellel, create multiple threads and split the tasks among the threads.

In the simple example below, we create one thread per task. If you have hundreds of tasks, or an unknown number of tasks, to execute, a thread pool (discussed below) may be a better option. 

Operators `+` and `.|` are automatically broadcasted over lists

```
aya> .# Create several tasks
aya> [ {"Thread 1 Start" :P  2000 :Z "Thread 1 End" :P 10}
       {"Thread 2 Start" :P  1000 :Z "Thread 2 End" :P 20}
       {"Thread 3 Start" :P  3000 :Z "Thread 3 End" :P 30}
       {"Thread 4 Start" :P  1500 :Z "Thread 4 End" :P 40} ] :tasks;

aya> .# Create a new thread for each task
aya> [tasks E R, ; threading.new] :threads;

aya> .# Add one task to each thread using automatic operator broadcasting
aya> .# The call below is the same as: [tasks threads, .add_task];
aya> tasks threads + ;
Thread 1 Start
Thread 3 Start
Thread 4 Start
Thread 2 Start
Thread 2 End
Thread 4 End
Thread 1 End
Thread 3 End

aya> .# Wait for each thread to complete
aya> .# The operator .| is automatically broadcasted to all threads
aya> .# The call below is the same as [threads, .wait_for_result] :P
aya> threads .| :P
[ [ 10 ] [ 20 ] [ 30 ] [ 40 ] ]

```

## Thread Pool

Executing many tasks as fast as possible is a common pattern and spawning a new thread for every task as in the example above may not always be the best solution. A better solution may be to use a fixed number of threads and distribute the tasks among the threads until all tasks are complete. 

The `threading` module provides a `threading.pool` object for this purpose. To create a thread pool, pass the number of threads to the `threading.pool` constructor.

```
aya> 2 threading.pool!

```

Same as the example above but using a thread pool instead. The second argument to `threading.pool.exec` is a callback function (see below). Leave empty to do nothing.

```
aya> .# Create several of tasks
aya> [ {"Thread 1 Start" :P  2000 :Z "Thread 1 End" :P 10}
       {"Thread 2 Start" :P  1000 :Z "Thread 2 End" :P 20}
       {"Thread 3 Start" :P  3000 :Z "Thread 3 End" :P 30}
       {"Thread 4 Start" :P  1500 :Z "Thread 4 End" :P 40} ] :tasks;

aya> .# Create a thread pool with 2 threads
aya> 2 threading.pool! :tpool;

aya> .# Execute all tasks
aya> tasks {} tpool.exec
Thread 4 Start
Thread 3 Start
Thread 4 End
Thread 2 Start
Thread 2 End
Thread 1 Start
Thread 3 End
Thread 1 End
```

The second argument to `threading.pool.exec` is the callback function to be executed on the result of each thread. For instance, we may want to collect the results of all tasks into a single list:

```
aya> .# Simple tasks that just return a number
aya> [ {10} {20} {30} {40} ] :tasks;

aya> 2 threading.pool! :tpool;

aya> []:accumulator;
aya> tasks {result, result accumulator .B} tpool.exec

aya> accumulator
[
  [ 40 ]
  [ 30 ]
  [ 20 ]
  [ 10 ]
]
```


## Communication

Each thread is essentially it's own instance of an Aya interpreter. While each thread has access to any object created by any other thread, these objects are not guaranteed to be in sync across threads. Instead, each task should be thought of as a standalone function that takes data in via the task block definition and sends data back via `wait_For_result`.

### Sending data to a thread

Each thread has its own variable scope. Threads cannot access variables from other scopes (including the main thread).

```
aya> .# define x in the main thread
aya> 10 :x;

aya> threading.new :t
( 1 ) thread!

aya> .# Attempt to access x in another thread
aya> { "x is $x" :P } t + ;
aya> t.wait_for_result
Undefined variable 'x'


{ "x is $x" :P } t.add_task ;
~~~~~~~~^
Function call traceback:
  File '<input>', line 1 in .wait_for_result:
    t.wait_for_result
    ~~~~~~~~~~~~~~~~^

```

To share a variable with a thread, it must be captured in the task block. `x` is now captured so the thread has access to it's value

```
aya> 10 :x;
aya> { : x^ , "x is $x" :P } t.add_task ;
x is 10
aya> t.wait_for_result
[ ]
```

Note that `^` captures the *value* of a variable so if a modification is made to the variable while a task is executing, the change will not be seen by the thread.

```
aya> 10 :x;

aya> .# wait for 10s in the thread
aya> { : x^ , "thread start" 10000:Z "x is $x" :P } t.add_task ;

aya> .# change x before the thread is finished
aya> 99 :x;

aya> .# The value is not changed in the thread, the original value is used
x is 10

```

For mutable data, changes are reflected but **no guarentees are made about the data being in sync across threads**. In general, mutable objects should not be updated if they are being shared between threads.

```
aya> {, 10 :a } :x;

aya> .# wait for 10s in the thread
aya> { : x^ , "thread start" 10000:Z "x is $x" :P } t.add_task ;

aya> .# Change `x.a` before the thread is finished
aya> .# Update a mutable object while it is being used in another thread
aya> .# Don't do this! Mutations are not guaranteed to be shared across threads
aya> 99 x.:a;

aya> `x.a` is `99` in the main thread
aya> x
{,
  99:a;
}

aya> .# When the other thread accesses `x`, `x.a` is `10`, sometimes `x.a` is `99`
x is {, 99:a; }
```

### Getting data from a thread

As stated above, modifying mutable objects should not be used to send data from one thread to another thread. Instead, data should be passed directly by using the stack and `wait_for_result`

```
aya> .# The task can leave as many objects on the stack as needed
aya> { 1 2 /   3 4 + } my_thread.add_task ;

aya> .# The complete stack is returned as a list
aya> my_thread.wait_for_result
[ .5 7 ]
```

