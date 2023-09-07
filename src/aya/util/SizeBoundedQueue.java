package aya.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class SizeBoundedQueue<T> extends ArrayDeque<T> {

	private final int maxNumElements;

	public SizeBoundedQueue(int maxNumElements) {
		super(maxNumElements);
		this.maxNumElements = maxNumElements;
	}

	/**
	 * Dequeues all elements to a list.
	 * @return a list containing all elements of this queue.
	 */
	public synchronized ArrayList<T> dequeToList() {
		ArrayList<T> list = new ArrayList<>(this);
		this.clear();
		return list;
	}

	/**
	 * Removes elements from this queue until it can accept an additional element without exceeding the size limit.
	 */
	private void ensureSizeLimit() {
		// assuming the queue does not exceed the size limit yet, at most one element must be removed
		if (this.size() >= maxNumElements) {
			this.remove();
		}
	}

	/**
	 * Removes elements from this queue until it can accept 'numAddedElements'-many additional elements without exceeding the size limit.
	 *
	 * @param numAddedElements number of elements to make room for.
	 */
	private void ensureSizeLimit(int numAddedElements) {
		int numElementsToRemove = this.size() + numAddedElements - maxNumElements;
		if (numElementsToRemove <= 0) {
			return;
		}

		for (int i = 0; i < numElementsToRemove; i++) {
			this.remove();
		}
	}

	@Override
	public void addFirst(T t) {
		ensureSizeLimit();
		super.addFirst(t);
	}

	@Override
	public void addLast(T t) {
		ensureSizeLimit();
		super.addLast(t);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		ensureSizeLimit(c.size());
		return super.addAll(c);
	}

	@Override
	public boolean add(T t) {
		ensureSizeLimit();
		return super.add(t);
	}
}
