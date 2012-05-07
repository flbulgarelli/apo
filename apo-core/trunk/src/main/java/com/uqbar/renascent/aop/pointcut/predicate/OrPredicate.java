package com.uqbar.renascent.aop.pointcut.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CtClass;
import javassist.CtField;

/**
 * 
 */
public class OrPredicate implements APredicate {
	private List<APredicate> components = new ArrayList<APredicate>();

	public OrPredicate(APredicate... predicate) {
		this.components = Arrays.asList(predicate);
	}

	@Override
	public boolean evaluate(CtClass ctClass) {
		for (APredicate component : this.components) {
			if (component.evaluate(ctClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean evaluate(CtField ctField) {
		for (APredicate component : this.components) {
			if (component.evaluate(ctField)) {
				return true;
			}
		}
		return false;
	}

}
