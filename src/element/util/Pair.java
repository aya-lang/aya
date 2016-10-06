package element.util;

public class Pair<T1, T2> {
	T1 a;
	T2 b;
	
	public Pair(T1 t1, T2 t2) {
		a = t1;
		b = t2;
	}
	
	public T1 first() {
		return a;
	}
	
	public T2 second() {
		return b;
	}
}
