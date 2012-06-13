package com.uqbar.renascent.framework.aop
import javassist.CtClass
import scala.collection.mutable.HashMap
import com.uqbar.renascent.aop.pointcut.predicate.APredicate
import javassist.expr.ExprEditor
import scala.collection.JavaConversions._
import javassist.ClassPool
import java.util.Map.Entry

/**
 * Colabora con el FrameworkClassLoader para construir los Aspectos que fueron declarados.
 * 
*/

trait TAdviceWeaver {
  
	private var weavingInstrumentors = new HashMap[APredicate, ExprEditor]();
	var strategy:IBehaviorAdviceWeaverStrategy = _

	protected def addInstrumentors(classPool:ClassPool) {
		strategy.addInstrumentors(classPool, weavingInstrumentors);
	}

	def applyAdvice(ctClass:CtClass) {
		if (!ctClass.isFrozen()) {
			this.weavingInstrumentors.entrySet().foreach(entry => {
				if (entry.getKey().evaluate(ctClass)) {
					applyAdviceToCtClass(ctClass, entry);
				}
				
				ctClass.getDeclaredFields().foreach(ctField =>{
					if (entry.getKey().evaluate(ctField)) {
						applyAdviceToCtClass(ctField.getType(), entry);
					}	
				})
			})
		}
	}

	def applyAdviceToCtClass(ctClass:CtClass , entry:Entry[APredicate, ExprEditor] ){
		strategy.applyAdviceToCtClass(ctClass, entry);
		ctClass.instrument(entry.getValue());
	}

}


class AdviceWeaver extends TAdviceWeaver{
  
  def this(classPool:ClassPool, strategy:IBehaviorAdviceWeaverStrategy ){
    this()
    this.strategy = strategy
    this.addInstrumentors(classPool)
  }
} 