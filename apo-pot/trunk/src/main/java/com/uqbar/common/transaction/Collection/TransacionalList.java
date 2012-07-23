package com.uqbar.common.transaction.Collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.uqbar.commons.utils.Transactional;

@Transactional
public class TransacionalList<E> extends TransactionalCollection<List<E>, E> implements List<E>{
	
	public TransacionalList(List<E> list) {
		this.setData(list);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		final boolean addAll = data.addAll(index, c);
		updateData();
		return addAll;

	}

	@Override
	public E get(int index) {
		return data.get(index);
	}

	@Override
	public E set(int index, E element) {
		final E set = data.set(index, element);
		updateData();
		return set;
	}

	@Override
	public void add(int index, E element) {
		data.add(index, element);
		updateData();		
	}

	@Override
	public E remove(int index) {
		final E remove = data.remove(index);
		updateData();
		return remove;
	}

	@Override
	public int indexOf(Object o) {
		return data.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return data.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return data.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return data.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return data.subList(fromIndex, toIndex);
	}

	@Override
	protected List<E> defauldValue() {
		return new ArrayList<E>();
	}
}
