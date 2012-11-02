package com.uqbar.aop.javassit.parser

import javassist.expr.FieldAccess
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import javassist.CtPrimitiveType

object JavassistParser {

  def parser(string: String): String = parser(null, string)

  def parser(fieldAccess: FieldAccess, string: String): String = {
    var result = string;
    Tokens.values().foreach(token => {
      if (result.contains(token.getRegExp())) {
        result = result.replace(token.getRegExp(), token.getJavassistExp(fieldAccess));
      }
    })

    return result;
  }

  def parser[T](expr: T, string: String): String = {
    expr match {
      case fieldAI: FieldAccess => parser(fieldAI, string)
      case _ => parser(null, string)
    }
  }

}

abstract class Token[T](val name: String) {
  def javassistExp(t: T): String
}

case object THIS extends Token[Any]("$this") {

  def javassistExp(t: Any) = "$0";
}

case object FIELD_NAME extends Token[{ def getFieldName(): String }]("$fieldName") {

  def javassistExp(field: { def getFieldName(): String }) = field.getFieldName()
}

case object FIELD_TYPE_NAME extends Token[FieldAccess]("$fieldTypeName") {

  def javassistExp(field: FieldAccess) = field.getField().getType().getName()
}

case object ARGUMENT extends Token[Any]("$argument") {

  def javassistExp(t: Any) = "$";
}

case object INTERCEPTOR extends Token[Any]("$interceptor") {

  def javassistExp(t: Any) = """((com.uqbar.aop.transaction.ObjectTransactionImpl) 
		  					com.uqbar.aop.transaction.ObjectTransactionManager.getTransaction())""";
}

case object STRINGS extends Token[Any]("'") {

  def javassistExp(t: Any) = "\"";
}

case object RETURN extends Token[Any]("$rtn") {

  def javassistExp(t: Any) = "$_ =";
}

case object DEFAULT_VALUE_ASSIGMENT extends Token[Any]("$defaultValueAssignment") {

  def javassistExp(t: Any) = ARGUMENT.javassistExp(t) + "1"
}

case object ORIGINAL_STATEMENT_ASSIGMENT extends Token[FieldAccess]("$originalAsigment") {

  def javassistExp(t: FieldAccess) = DEFAULT_FIELD.javassistExp(t) + " = " + DEFAULT_VALUE_ASSIGMENT.javassistExp(t) + ";";
}

case object ORIGINAL_STATEMENT_READER extends Token[FieldAccess]("$originalReader") {

  def javassistExp(t: FieldAccess) = RETURN.javassistExp(t) + " " + DEFAULT_FIELD.javassistExp(t) + ";";
}

case object DEFAULT_FIELD extends Token[FieldAccess]("$defaultField") {

  def javassistExp(fieldAccess: FieldAccess) = THIS.javassistExp(fieldAccess) + "." + FIELD_NAME.javassistExp(fieldAccess);
}

case object OLD_VALUE extends Token[FieldAccess]("$oldValue") {

  def javassistExp(fieldAccess: FieldAccess) = THIS.javassistExp(fieldAccess) + ".get" + FIELD_NAME.javassistExp(fieldAccess).capitalize + "();";
}

case object OBJECT extends Token[Any]("$Object") {

  def javassistExp(a: Any) = classOf[Object].getName()
}

case object NEW_VALUE extends Token[Any]("$newValue") {

  def javassistExp(a: Any) = ARGUMENT.javassistExp(a) + "1";
}

case object COERCE_TO_OBJECT extends Token[FieldAccess]("$coerceToObject") {

  def javassistExp(field: FieldAccess): String = {
    var typ = field.getField().getType();
    if (typ.isPrimitive()) "new " + typ.asInstanceOf[CtPrimitiveType].getWrapperName() else ""

  }
}

