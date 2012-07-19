package com.uqbar.aop.pointcut.predicate
import javassist.CtClass
import javassist.ClassPool
import javassist.CtField

/**
 * Predicate que recibe un CtClass y evalua si la esa clase es hija en cualquier nivel de la clase con la
 * cual se construye
 *
 */
class SuperClassPredicate extends APredicate {

  private var superClass: CtClass = _
  private var superClassName: String = _
  private var classPool: ClassPool = _

  def this(classPool: ClassPool, superClassName: String) {
    this()
    this.superClassName = superClassName;
    this.classPool = classPool;
  }

  override def evaluate(ctClass: CtClass): Boolean = {
    if (ctClass == null) {
      return false;
    }
    if (this.superClass == null) {
      this.superClass = this.classPool.get(this.superClassName);
    }
    return ctClass.subtypeOf(this.superClass);
  }

  override def evaluate(ctField: CtField) = false
}