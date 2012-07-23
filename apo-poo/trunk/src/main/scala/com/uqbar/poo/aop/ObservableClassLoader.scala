package com.uqbar.poo.aop
import javassist.ClassPool
import com.uqbar.aop.APOClassLoader
import com.uqbar.aop.AdviceWeaver

/**
 * Nuestro classloader, que al cargar una clase, le hace weaving para meterle la magia de aspectos.
 * 
 * 
 * -Djava.system.class.loader=com.uqbar.poo.aop.ObservableClassLoader
 */
class ObservableClassLoader(parent:ClassLoader) extends APOClassLoader(parent){

	override def createAdviceWeaver(cp:ClassPool):AdviceWeaver = {
		return new AdviceWeaver(cp, new ObservableBehaviorAdviceWeaverStrategy());
	}
	
}
