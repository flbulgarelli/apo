package com.uqbar.pot.aop

import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.FieldAccess;

import org.apache.commons.lang.StringUtils;

import com.uqbar.aop.FieldAccessInterceptor;
import com.uqbar.aop.javassit.parser.Tokens;


class TransactionFieldAccessInterceptor extends FieldAccessInterceptor {

	override def modifyWriterFieldAccess(statement:StringBuffer , fieldAccess:FieldAccess ) {
		if(!Modifier.isTransient(fieldAccess.getField().getModifiers())){
		  var newExpresion  = 
		    """
		    $defaultField = ($fieldTypeName) $interceptor.fieldWrite($this, '$fieldName', $argument1, $this.$fieldName);
		    """
			var reemplaze = Tokens.ORIGINAL_STATEMENT_ASSIGMENT.getRegExp();
			
			statement.replace(0, statement.length(), StringUtils.replace(statement.toString(), reemplaze, newExpresion));
		}
	}

	override def modifyReaderFieldAccess(statement:StringBuffer , fieldAccess:FieldAccess){
		var reemplaze = Tokens.ORIGINAL_STATEMENT_READER.getRegExp();

		var newExpresion = "$rtn ($fieldTypeName) $interceptor.fieldRead($this, '$fieldName', $this.$fieldName);";
		
		statement.replace(0, statement.length(), StringUtils.replace(statement.toString(), reemplaze, newExpresion));
	}

	override def getSpecificPropertyKey() = "TransactionFieldAccessInterceptor"
}