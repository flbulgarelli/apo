package com.uqbar.apo

import scala.collection.mutable.Buffer
import org.apache.commons.lang.StringUtils
import javassist.expr.FieldAccess
import javassist.CtMethod

/**
 * 
 * @author nny
 * @param <A>
 */
//TODO hacer que se pueda componer los interceptors
trait Interceptor[A] {

  lazy val isEnabled = APOConfig.isEnable(this.getSpecificPropertyKey())
  val intercepts = Buffer[Behavior[_]]()

  def before(fun: (A) => String) = intercepts.append(Before(fun))
  def after(fun: (A) => String) = intercepts.append(After(fun))
  def arround(fun: (StringBuffer, A) => Unit) = intercepts.append(Arround(fun))

  def intercept(statement: StringBuffer, a: A)

  /**
   * Utilizado para armar la key de habilitación en el framework-config.properties
   */
  protected def getSpecificPropertyKey(): String = ""

  def getType: java.lang.Class[A]
  
  implicit def replace(buffer:StringBuffer)= new StringBufferWriter(buffer)
}

class StringBufferWriter(buffer:StringBuffer){
  def replace(reemplaze:String, newExpresion:String){
    this.replace(StringUtils.replace(buffer.toString(), reemplaze, newExpresion));
  }
  
  def replace(reemplaze:String){
    buffer.replace(0, buffer.length(), reemplaze);
  }
}

trait FieldInterceptor extends Interceptor[FieldAccess] {

  def read(fun: (StringBuffer, FieldAccess) => Unit) = intercepts.append(ReadField(fun))
  def write(fun: (StringBuffer, FieldAccess) => Unit) = intercepts.append(WriteField(fun))

  def intercept(statement: StringBuffer, field: FieldAccess) {
    var filter = Buffer[Behavior[FieldAccess]]()
    if (field.isWriter()) {
      filter = intercepts.filter(_.isInstanceOf[WriteField]).asInstanceOf[Buffer[Behavior[FieldAccess]]]
    } else {
      filter = intercepts.filter(_.isInstanceOf[ReadField]).asInstanceOf[Buffer[Behavior[FieldAccess]]]
    }
    filter.foreach(call => call.proceed(statement, field))
  }

  def getType = classOf[FieldAccess]
}

trait MethodInterceptor extends Interceptor[CtMethod] {

  def intercept(statement: StringBuffer, method: CtMethod) {
    intercepts.asInstanceOf[Seq[Behavior[CtMethod]]].foreach(call => call.proceed(statement, method))
  }

  def getType  = classOf[CtMethod]

}
