package com.uqbar.common.transaction.Collection;

import java.util.Collection;
import java.util.Iterator;


public abstract class TransactionalCollection<D extends Collection<E>, E> extends TransactionalUtil<D> implements Collection<E>{
	
	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return data.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return data.iterator();
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean add(E e) {
		final boolean add = data.add(e);
		updateData();
		return add;
	}


	@Override
	public boolean remove(Object o) {
		final boolean remove = data.remove(o);
		updateData();
		return remove;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		final boolean addAll = data.addAll(c);
		updateData();
		return addAll;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
 		final boolean removeAll = data.removeAll(c);
		updateData();
		return removeAll;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		final boolean retainAll = data.retainAll(c);
		updateData();
		return retainAll;

	}

	@Override
	public void clear() {
		data.clear();
		updateData();
	}
}
