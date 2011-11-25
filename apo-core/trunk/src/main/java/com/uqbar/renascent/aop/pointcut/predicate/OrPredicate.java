package com.uqbar.renascent.aop.pointcut.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.uqbar.commons.collections.Predicate;

/**
 * 
 * @author jfernandes
 */
public class OrPredicate<T> implements Predicate<T> {
	private List<Predicate<T>> components = new ArrayList<Predicate<T>>();
	
	public OrPredicate(Predicate<T>... predicate) {
		this.components = Arrays.asList(predicate);
	}

	@Override
	public boolean evaluate(T object) {
		for (Predicate<T> component : this.components) {
			if (component.evaluate(object)) {
				return true;
			}
		}
		return false;
	}

}
