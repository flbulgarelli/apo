package com.uqbar.aop.pointcut.predicate

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import scala.collection.JavaConversions._

import com.uqbar.commons.exceptions.ProgramException;

/**
 * Evaluates whether the given CtClass has been annotated with a given annotation class.
 *
 */

class HasAnnotationPredicate extends APredicate {

  private var annotationType: CtClass = _
  var annotationTypeName: String = _
  var classPool: ClassPool = _

  def this(classPool: ClassPool, annotationTypeName: String) {
    this()
    this.classPool = classPool;
    this.annotationTypeName = annotationTypeName;
  }

  override def evaluate(clazz: CtClass): Boolean = {
    clazz.getAnnotations().foreach(annotationProxy => {
      if (implementsAnnotationInterface(annotationProxy.getClass().getInterfaces())) {
        return true;
      }
    })
    return false;
  }

  def implementsAnnotationInterface[A](interfaces: Array[Class[_]]): Boolean = {
    interfaces.foreach(interface => {
      if (this.annotationTypeName.equals(interface.getName())) {
        return true;
      }
    })
    return false;
  }

  def getAnnotationType: CtClass = {
    if (this.annotationType == null) {
      this.annotationType = this.classPool.get(this.annotationTypeName);
    }
    return this.annotationType;
  }

  override def evaluate(ctField: CtField): Boolean = {
    ctField.getAnnotations().foreach(annotationProxy => {
      if (implementsAnnotationInterface(annotationProxy.getClass().getInterfaces())) {
        return true;
      }
    })
    return false;
  }

}