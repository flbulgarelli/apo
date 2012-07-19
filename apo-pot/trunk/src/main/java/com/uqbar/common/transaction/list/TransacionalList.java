package com.uqbar.common.transaction.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.uqbar.commons.utils.Transactional;

@Transactional
public class TransacionalList<E>  implements List<E>{
	
	transient private List<E> data;

	public TransacionalList(List<E> list) {
		this.setData(list);
	}

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

	private void updateData() {
		final List<E> data2 = data;
		data = null;
		data = data2;
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
	public boolean addAll(int index, Collection<? extends E> c) {
		final boolean addAll = data.addAll(index, c);
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

	public List<E> getData() {
		return data;
	}

	public void setData(List<E> aData) {
		if(aData == null){
			this.data = new ArrayList<E>();
		}else{
			this.data = aData;
		}
	}

}
