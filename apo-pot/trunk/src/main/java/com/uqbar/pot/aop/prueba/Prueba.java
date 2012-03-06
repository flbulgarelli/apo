package com.uqbar.pot.aop.prueba;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Prueba {
	
	private Collection<Object> collection ;
	
	
	public void sinAspectos() {
		this.collection = new ArrayList<Object>();
	}
	
	@SuppressWarnings("unchecked")
	public void conAspectos() {
		final Collection<Object> newValue = new ArrayList<Object>();
		this.collection = (Collection<Object>) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{newValue.getClass()}, new InvocationHandler() {
			
			private Object clone;
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(clone == null){
					clone = createClone(newValue);
				}
				return method.invoke(clone, args);
			}

			private Object createClone(Collection<Object> newValue) throws Throwable{
				Class constructorParameter = newValue instanceof Collection? Collection.class : Map.class;
				return newValue.getClass().getConstructor(constructorParameter).newInstance(newValue);
			}
		});
	}

}
