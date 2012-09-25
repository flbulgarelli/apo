package com.uqbar.aop

import javassist.CtClass
import scala.collection.mutable.HashMap
import com.uqbar.aop.pointcut.predicate.PointCut
import javassist.expr.ExprEditor
import scala.collection.JavaConversions._
import javassist.ClassPool
import java.util.Map.Entry
import scala.collection.mutable.Buffer

/**
 * Colabora con el FrameworkClassLoader para construir los Aspectos que fueron declarados.
 *
 */

abstract class AdviceWeaver {
  
  var classPool: ClassPool =_
  val advices = Buffer[Advice]()

  def this(cp: ClassPool) {
    this()
    this.classPool = cp
  }
  
  def init(){
	  this.configureAdvices()
  }

  def configureAdvices() {}
  def configureJoinPoint(joinPoint: JoinPoint) {}
  
  def doApplyAdviceToCtClass(ctClass:CtClass, advice: Advice) {
  }

  def applyAdvice(ctClass: CtClass) {
    if (!ctClass.isFrozen()) {
      this.advices.foreach(advice => {
        if (advice.pointCut.evaluate(ctClass)) {
          advice.joinPoint.pointCut = advice.pointCut
          applyAdviceToCtClass(ctClass, advice);
        }
      })
    }
  }

  def applyAdviceToCtClass(ctClass: CtClass, advice: Advice) {
    doApplyAdviceToCtClass(ctClass, advice);
    ctClass.instrument(advice.joinPoint);
  }

}