package com.uqbar.renascent.framework.aop;

import javassist.ClassPool;

import com.uqbar.renascent.framework.aop.AdviceWeaver;
import com.uqbar.renascent.framework.aop.FrameworkClassLoader;

/**
 * Nuestro classloader, que al cargar una clase, le hace weaving para meterle la magia de aspectos.
 * 
 * Parametro para correr tests con este ClassLoader.
 * 
 * -Djava.system.class.loader=com.uqbar.renascent.framework.aop.ObjectTransactionClassLoader
 */
public class ObjectTransactionClassLoader extends FrameworkClassLoader{

	public ObjectTransactionClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	protected AdviceWeaver createAdviceWeaver(ClassPool cp) {
		return new AdviceWeaver(cp, new TransactionalBehaviorAdviceWeaverStrategy());
	}
	
}
