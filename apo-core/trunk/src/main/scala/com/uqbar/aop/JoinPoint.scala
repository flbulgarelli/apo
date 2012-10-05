package com.uqbar.aop

import scala.collection.mutable.Buffer
import scala.reflect.Type
import com.uqbar.aop.javassit.parser.JavassistParser
import com.uqbar.aop.javassit.parser.Tokens
import com.uqbar.aop.pointcut.PointCut
import javassist.expr.Expr
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import javassist.CannotCompileException
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import scala.reflect.Manifest

/**
 * Intruduce bytecode para que se pueda interceptar todos los fields del objeto.
 *
 */

class JoinPoint extends ExprEditor {
  /**
   * Lista con FieldAccessInterceptors, para armar el codigo que se va a agregar a los accesors.
   */
  // DESIGN-REFACTOR: que se inyecten por IOC
  val interceptors = Buffer[Interceptor[_]]()
  lazy val aopEnabled = AopConfig.isAOPEnable()
  var pointCut: PointCut = _

  // ***************************
  // ** FieldAccess weaving
  // ***************************

  override def edit(fieldAccess: FieldAccess) {
    var statement = new StringBuffer();
    if (fieldAccess.isWriter()) {
      statement.append(Tokens.ORIGINAL_STATEMENT_ASSIGMENT.getRegExp())
    }
    if (fieldAccess.isReader()) {
      statement.append(Tokens.ORIGINAL_STATEMENT_READER.getRegExp())
    }

    val isEdit = edit[FieldAccess](fieldAccess, statement, classOf[FieldAccess])
    if (isEdit)
      fieldAccess.replace(JavassistParser.parser(fieldAccess, statement.toString()))
  }

  def edit(method: CtMethod) {
    var statement = new StringBuffer(method.getSignature())
    edit[CtMethod](method, statement, classOf[CtMethod])
  }

  /**
   * @param fieldAccess
   * @param name Name of the field being processed
   */
  def edit[T:Manifest](expr: T, statement: StringBuffer, classType: Class[T]): Boolean = {
    var edit = false;
    if (aopEnabled) {
      try {
        filterInterceptor[T]().foreach(interceptor => {
          if (pointCut.hasIntercept(expr)) {
            interceptor.intercept(statement, expr)
            edit = true;
          }
        })
      } catch {
        case e: CannotCompileException => throw this.getVerbosedException(e, statement.toString(), expr);
        case e: NotFoundException => throw this.getVerbosedException(e, statement.toString(), expr);
      }
    }
    edit
  }

  def instrument(ctClass: CtClass) {
    ctClass.instrument(this);
    ctClass.getMethods().foreach(m => this.edit(m))
  }

  /**
   * Evita agregar el aspecto a los field access:
   * <ul>
   * <li>Estáticos.
   * <li>Que ocurran dentro de un constructor.
   * </ul>
   */
  def mustWeave(fieldAcces: FieldAccess): Boolean = {
    return !fieldAcces.isStatic() && //
      !fieldAcces.where().getMethodInfo().toString().startsWith("<init>") && this.aopEnabled;
  }

  // ***************************
  // ** Exception helpers
  // ***************************

  //TODO refactorizar esto
  def filterInterceptor[T:Manifest](): Buffer[Interceptor[T]] = {
    val filter = Buffer[Interceptor[T]]()
    this.interceptors.foreach(interceptor => {
      if (interceptor.getType.equals(manifest[T].erasure)) {
        filter.append(interceptor.asInstanceOf[Interceptor[T]])
      }
    })

    return filter
  }

  def getVerbosedException[T](ex: Exception, javaStatement: String, expr: T): RuntimeException = {
    return new RuntimeException(javaStatement, ex);
  }

  def addInterceptor[A](interceptor: Interceptor[A]): JoinPoint = {
    this.interceptors.append(interceptor);
    return this;
  }
}