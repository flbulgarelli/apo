package com.uqbar.aop

import java.util.ArrayList
import javassist.CannotCompileException
import javassist.NotFoundException
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import com.uqbar.aop.javassit.parser.JavassistParser
import com.uqbar.aop.javassit.parser.Tokens
import scala.collection.mutable.Buffer
import javassist.expr.Expr
import scala.reflect.Type
import javassist.expr.MethodCall
import com.uqbar.aop.pointcut.predicate.PointCut

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

  override def edit(methodCall: MethodCall) {
    var statement = new StringBuffer(methodCall.getMethod().getSignature())
    edit[MethodCall](methodCall, statement, classOf[MethodCall])
  }

  /**
   * @param fieldAccess
   * @param name Name of the field being processed
   */
  def edit[T <: Expr](expr: T, statement: StringBuffer, classType: Class[T]): Boolean = {
    var edit = false;
    if (aopEnabled) {
      try {
        filterInterceptor(classType).foreach(interceptor => {
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
  def filterInterceptor[T <: Expr](classOfT: java.lang.Class[T]): Buffer[Interceptor[T]] = {
    val filter = Buffer[Interceptor[T]]()
    this.interceptors.foreach(interceptor => {
      if (interceptor.getType.equals(classOfT)) {
        filter.append(interceptor.asInstanceOf[Interceptor[T]])
      }
    })

    return filter
  }

  def getVerbosedException(ex: Exception, javaStatement: String, expr: Expr): RuntimeException = {
    return new RuntimeException(ex.getMessage() + "\ncontainerClassName:" + expr.getEnclosingClass().getName()
      + "\nlineNumber" + expr.getLineNumber() + "\ncontainerMethod" + expr.where().getMethodInfo()
      + "\nJavassist Statement" + javaStatement, ex);
  }

  def addInterceptor[I <: Expr](interceptor: Interceptor[I]): JoinPoint = {
    this.interceptors.append(interceptor);
    return this;
  }
}