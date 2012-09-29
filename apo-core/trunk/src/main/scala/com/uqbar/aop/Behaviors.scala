package com.uqbar.aop
import javassist.expr.FieldAccess
import com.uqbar.aop.javassit.parser.JavassistParser
import javassist.expr.Expr
import javassist.CtBehavior

abstract class Behavior[E](var call: (E) => String) {
  def proceed(statement: StringBuffer, e: E)

  def parse(code: String, e: E) = JavassistParser.parser(e, code)
}

case class Before[E <: CtBehavior](f: (E) => String) extends Behavior[E](f) {
  def proceed(statement: StringBuffer, e: E) = e.insertBefore(parse(call(e), e))
}
case class After[E <: CtBehavior](f: (E) => String) extends Behavior[E](f) {
  def proceed(statement: StringBuffer, e: E) = e.insertAfter(parse(call(e), e))
}
case class Arround[E](edit: (StringBuffer, E) => Unit) extends Behavior[E](null) {
  def proceed(statement: StringBuffer, e: E) = edit(statement, e)
}
case class ReadField(f: (StringBuffer, FieldAccess) => Unit) extends Arround[FieldAccess](f)
case class WriteField(f: (StringBuffer, FieldAccess) => Unit) extends Arround[FieldAccess](f)
