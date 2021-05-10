package aya.util;

import java.util.ArrayList;

public class CircleIterator<T> {

	private ArrayList<T> items_;
	private int index_;

	public CircleIterator(ArrayList<T> items) {
		items_ = items;
		index_ = -1; // first next() is 0
	}

	public T next() {
		index_ = (index_ + 1) % items_.size();
		return items_.get(index_);
	}
}
