package com.uqbar.pot.aop;

import javassist.ClassPool;

import com.uqbar.aop.AdviceWeaver;
import com.uqbar.aop.APOClassLoader;

/**
 * Nuestro classloader, que al cargar una clase, le hace weaving para meterle la magia de aspectos.
 * 
 * Parametro para correr tests con este ClassLoader.
 * 
 * -Djava.system.class.loader=com.uqbar.pot.aop.ObjectTransactionClassLoader
 */
public class ObjectTransactionClassLoader extends APOClassLoader{

	public ObjectTransactionClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	protected AdviceWeaver createAdviceWeaver(ClassPool cp) {
		return new AdviceWeaver(cp, new TransactionalBehaviorAdviceWeaverStrategy());
	}
	
}
