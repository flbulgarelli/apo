package com.uqbar.aop
import javassist.expr.FieldAccess
import com.uqbar.aop.javassit.parser.JavassistParser
import javassist.expr.Expr

abstract class Behavior[E <: Expr](var call: (E) => String) {
  def proceed(statement: StringBuffer, e: E)

  def parse(code: String, e: E) = JavassistParser.parser(e, code)
}

case class Before[E <: Expr](f: (E) => String) extends Behavior[E](f) {
  def proceed(statement: StringBuffer, e: E) = e.where().insertBefore(parse(call(e), e))
}
case class After[E <: Expr](f: (E) => String) extends Behavior[E](f) {
  def proceed(statement: StringBuffer, e: E) = e.where().insertAfter(parse(call(e), e))
}
case class Arround[E <: Expr](edit: (StringBuffer, E) => Unit) extends Behavior[E](null) {
  def proceed(statement: StringBuffer, e: E) = edit(statement, e)
}
case class ReadField(f: (StringBuffer, FieldAccess) => Unit) extends Arround[FieldAccess](f)
case class WriteField(f: (StringBuffer, FieldAccess) => Unit) extends Arround[FieldAccess](f)
