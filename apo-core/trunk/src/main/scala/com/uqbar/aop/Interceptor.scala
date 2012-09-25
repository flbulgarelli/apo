package com.uqbar.aop
import javassist.expr.Expr
import scala.collection.mutable.Buffer
import com.uqbar.aop.javassit.parser.JavassistParser
import javassist.expr.FieldAccess
import javassist.expr.MethodCall

//TODO hacer que se pueda componer los interceptors
trait Interceptor[A <: Expr] {

  lazy val isEnabled = AopConfig.isEnable(this.getSpecificPropertyKey())
  val intercepts = Buffer[Behavior[A]]()

  def before(fun: (A) => String) = intercepts.append(Before(fun))
  def after(fun: (A) => String) = intercepts.append(After(fun))
  def arround(fun: (StringBuffer, A) => Unit) = intercepts.append(Arround(fun))

  def intercept(statement: StringBuffer, a: A)

  /**
   * Utilizado para armar la key de habilitación en el framework-config.properties
   */
  protected def getSpecificPropertyKey(): String

  def getType: java.lang.Class[A]
}

trait FieldInterceptor extends Interceptor[FieldAccess] {

  def read(fun: (StringBuffer, FieldAccess) => Unit) = intercepts.append(ReadField(fun))
  def write(fun: (StringBuffer, FieldAccess) => Unit) = intercepts.append(WriteField(fun))

  def intercept(statement: StringBuffer, field: FieldAccess) {
    var filter = Buffer[Behavior[FieldAccess]]()
    if (field.isWriter()) {
      filter = intercepts.filter(call => call match { case r: WriteField => true case _ => false })
    } else {
      filter = intercepts.filter(call => call match { case r: ReadField => true case _ => false })
    }
    filter.foreach(call => call.proceed(statement, field))
  }

  def getType = classOf[FieldAccess]
}

trait MethodInterceptor extends Interceptor[MethodCall] {

  def intercept(statement: StringBuffer, method: MethodCall) {
    intercepts.foreach(call => call.proceed(statement, method))
  }

  def getType  = classOf[MethodCall]

}
