package com.uqbar.aop.pointcut.predicate
import scala.collection.mutable.Buffer
import javassist.CtClass
import javassist.CtField

class OrPredicate extends APredicate {

  var components = Buffer[APredicate]();

  def this(predicate: APredicate*) {
    this()
    this.components = predicate.toBuffer
  }

  override def evaluate(ctClass: CtClass): Boolean = {
    this.components.foreach(component => {
      if (component.evaluate(ctClass)) {
        return true;
      }
    })
    return false;
  }

  override def evaluate(ctField: CtField): Boolean = {
    this.components.foreach(component => {
      if (component.evaluate(ctField)) {
        return true;
      }
    })
    return false;
  }

}