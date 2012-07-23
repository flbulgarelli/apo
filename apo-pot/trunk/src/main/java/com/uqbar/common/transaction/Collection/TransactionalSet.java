package com.uqbar.common.transaction.Collection;

import java.util.HashSet;
import java.util.Set;

public class TransactionalSet<T> extends TransactionalCollection<Set<T>, T> implements Set<T>{
	
	public TransactionalSet(Set<T> set) {
		setData(set);
	}

	@Override
	protected Set<T> defauldValue() {
		return new HashSet<T>();
	}
}
