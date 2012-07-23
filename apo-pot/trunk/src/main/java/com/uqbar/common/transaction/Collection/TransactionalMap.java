package com.uqbar.common.transaction.Collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransactionalMap<K, V> extends TransactionalUtil<Map<K, V>> implements Map<K, V>{
	
	public TransactionalMap(Map<K,V> map) {
		this.setData(map);
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
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return data.get(key);
	}

	@Override
	public V put(K key, V value) {
		final V put = data.put(key, value);
		updateData();
		return put;
	}

	@Override
	public V remove(Object key) {
		final V remove = data.remove(key);
		updateData();
		return remove;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		data.putAll(m);
		updateData();
	}

	@Override
	public void clear() {
		data.clear();
		updateData();
	}

	@Override
	public Set<K> keySet() {
		return data.keySet();
	}

	@Override
	public Collection<V> values() {
		return data.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return data.entrySet();
	}

	@Override
	protected Map<K, V> defauldValue() {
		return new HashMap<K, V>();
	}
	
}
