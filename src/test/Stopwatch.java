package test;

public class Stopwatch {
	long _start;
	long _elapsed;
	
	public Stopwatch() {}
	
	public void start() {
		_start = System.currentTimeMillis();
	}
	
	public void stop() {
		_elapsed = System.currentTimeMillis() - _start;
	}
	
	public double secs() {
		return ((double)_elapsed/1000);
	}
}
