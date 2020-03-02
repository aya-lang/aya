package aya.util;

public class Triple<T1, T2, T3> {
	T1 a;
	T2 b;
	T3 c;
	
	public Triple(T1 t1, T2 t2, T3 t3) {
		a = t1;
		b = t2;
		c = t3;
	}
	
	public T1 first() {
		return a;
	}
	
	public T2 second() {
		return b;
	}
	
	public T3 third() {
		return c;
	}
}
