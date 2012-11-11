package com.uqbar.apo

import javassist.ClassPool
import javassist.CtClass

/**
 * Colabora con el FrameworkClassLoader para construir los Aspectos que fueron declarados.
 *
 */

class AdviceWeaver {
  var classPool: ClassPool =_
  var advices:List[Advice] = _

  def this(cp: ClassPool) {
    this()
    this.classPool = cp
  }
  
  def init(){
	  val configurationClass:Class[Configuration] =  Class.forName(APOConfig.getAOPConfigClass()).asInstanceOf[Class[Configuration]]
	  val adviceConfiguration = configurationClass.newInstance()
	  this.advices = adviceConfiguration.createAdvices();
  }

  def applyAdvice(ctClass: CtClass) {
    if (!ctClass.isFrozen()) {
      this.advices.foreach(advice => {
        if (advice.pointCut.evaluate(ctClass)) {
          applyAdviceToCtClass(ctClass, advice);
        }
      })
    }
  }

  def applyAdviceToCtClass(ctClass: CtClass, advice: Advice) {
    advice.apply(ctClass)
  }
}