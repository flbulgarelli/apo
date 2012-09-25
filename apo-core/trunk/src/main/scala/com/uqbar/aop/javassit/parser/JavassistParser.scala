package com.uqbar.aop.javassit.parser
import javassist.expr.FieldAccess
import javassist.expr.Expr

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
  
  def parser(expr: Expr, string: String): String = {
    expr match {
      case fieldAI:FieldAccess => parser(fieldAI, string)
      case _ => parser(null, string)
    }
  }

}