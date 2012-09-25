package com.uqbar.pot.aop

import javassist.Modifier
import javassist.NotFoundException
import javassist.expr.FieldAccess
import org.apache.commons.lang.StringUtils
import com.uqbar.aop.javassit.parser.Tokens
import com.uqbar.aop.Interceptor
import com.uqbar.aop.FieldInterceptor

class TransactionFieldInterceptor extends FieldInterceptor {

  write((statement, field) => {
    if (!Modifier.isTransient(field.getField().getModifiers())) {
      var newExpresion =
        """
		  $defaultField = ($fieldTypeName) $interceptor.fieldWrite($this, '$fieldName', $argument1, $this.$fieldName);
		"""
      var reemplaze = Tokens.ORIGINAL_STATEMENT_ASSIGMENT.getRegExp();
      statement.replace(0, statement.length(), StringUtils.replace(statement.toString(), reemplaze, newExpresion));
    }
  })

  read((statement, field) => {
    var reemplaze = Tokens.ORIGINAL_STATEMENT_READER.getRegExp();
    var newExpresion = "$rtn ($fieldTypeName) $interceptor.fieldRead($this, '$fieldName', $this.$fieldName);";
    statement.replace(0, statement.length(), StringUtils.replace(statement.toString(), reemplaze, newExpresion));
  })

  override def getSpecificPropertyKey() = "TransactionFieldAccessInterceptor"
}