package com.uqbar.apo.pointcut

import javassist.CtField
import javassist.CtClass
import javassist.expr.FieldAccess
import javassist.expr.MethodCall
import scala.collection.mutable.Buffer
import javassist.ClassPool
import javassist.CtMethod

abstract class PointCut {
  def evaluate(ctClass: CtClass): Boolean
  def hasIntercept(any: Any): Boolean = false
}

trait InterceptMatchPointCut[T] extends PointCut {
  var matchersIntercept = Buffer[(T) => Boolean]();

  override def hasIntercept(any: Any): Boolean = {
    super.hasIntercept(any) || (any match {
      case t: T => !matchersIntercept.exists(f => !f(t))
      case _ => false;
    })
  }

  def matchIntercept(fun: (T) => Boolean) = matchersIntercept.append(fun)
}

trait FieldPointCut extends PointCut with InterceptMatchPointCut[FieldAccess] {
  matchIntercept((field) => { !field.isStatic() && !field.where().getMethodInfo().toString().startsWith("<init>") })

  def matchFieldName(fun: (String) => Boolean) = matchIntercept(field => fun(field.getFieldName()))
  def matchField(fun: (FieldAccess) => Boolean) = matchIntercept(field => fun(field))
  def matchFieldType[T: Manifest] = matchIntercept(_.getField().getType().getName() == manifest[T].erasure.getName())
}

trait MethodPointCut extends MatchPointCut with InterceptMatchPointCut[CtMethod] {
  def matchMethodName(fun: (String) => Boolean) = matchIntercept(method => fun(method.getName()))
  def matchMethod(fun: (CtMethod) => Boolean) = matchIntercept(fun)

}

trait MatchPointCut extends PointCut {
  var matchs = Buffer[(CtClass) => Boolean]();
  override def evaluate(ctClass: CtClass) = !matchs.exists(f => !f(ctClass))
}

trait ClassPointCut extends MatchPointCut {
  def matchClassName(fun: (String) => Boolean) = matchs.append((ctClass) => fun(ctClass.getSimpleName()))
  def matchPackage(fun: (String) => Boolean) = matchs.append((ctClass) => fun(ctClass.getPackageName()))
  def isSuperClas(className: String) = matchs.append((ctClass) => isSuperClass(ctClass, className))

  protected def isSuperClass(ctClass: CtClass, className: String): Boolean = {
    val superClass = ctClass.getClassPool().get(className);
    if (superClass != null) {
      ctClass.subtypeOf(superClass);
    } else {
      false
    }
  }
}

trait AnnotationPointCut extends MatchPointCut {
  def hasAnnotation(annotation: String): Unit = matchs.append((ctClass) => hasAnnotation(ctClass, annotation))

  protected def hasAnnotation(clazz: CtClass, annotation: String): Boolean = {
    !clazz.isAnnotation() && clazz.getAvailableAnnotations().exists(annotationProxy => {
      isImplementsAnnotation(annotationProxy.getClass().getInterfaces(), annotation)
    })
  }

  def isImplementsAnnotation[A](interfaces: Array[Class[_]], annotation: String): Boolean = {
    interfaces.exists(_.getName().equals(annotation))
  }

}

class OrPointCut extends PointCut {
  var components = List[PointCut]();

  def this(predicate: PointCut*) {
    this()
    this.components = predicate.toList
  }

  override def evaluate(ctClass: CtClass) = this.components.foldLeft(false)((c, point) => c || point.evaluate(ctClass))
}