package com.uqbar.aop

import javassist.CtClass
import scala.collection.mutable.HashMap
import com.uqbar.aop.pointcut.predicate.APredicate
import javassist.expr.ExprEditor
import scala.collection.JavaConversions._
import javassist.ClassPool
import java.util.Map.Entry
import scala.collection.mutable.Buffer

/**
 * Colabora con el FrameworkClassLoader para construir los Aspectos que fueron declarados.
 *
 */

class AdviceWeaver {

  private var advices = Buffer[Advice]();
  var strategy: BehaviorAdviceWeaverStrategy = _

  def this(classPool: ClassPool, strategy: BehaviorAdviceWeaverStrategy) {
    this()
    this.strategy = strategy
    this.addInstrumentors(classPool)
  }

  protected def addInstrumentors(classPool: ClassPool) {
    strategy.addAdvices(classPool, advices);
  }

  def applyAdvice(ctClass: CtClass) {
    if (!ctClass.isFrozen()) {
      this.advices.foreach(advice=> {
        if (advice.predicate.evaluate(ctClass)) {
          applyAdviceToCtClass(ctClass, advice);
        }

        ctClass.getDeclaredFields().foreach(ctField => {
          if (advice.predicate.evaluate(ctField)) {
            applyAdviceToCtClass(ctField.getType(), advice);
          }
        })
      })
    }
  }

  def applyAdviceToCtClass(ctClass: CtClass, advice: Advice) {
    strategy.applyAdviceToCtClass(ctClass, advice);
    ctClass.instrument(advice.instrument);
  }

}