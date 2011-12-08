package com.uqbar.pot.aop;

import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.FieldAccess;

import org.apache.commons.lang.StringUtils;

import com.uqbar.aop.javassit.parser.Tokens;
import com.uqbar.renascent.framework.aop.FieldAccessInterceptor;


/**
 * @author
 */
public class TransactionFieldAccessInterceptor extends FieldAccessInterceptor {

	@Override
	protected void modifyWriterFieldAccess(StringBuffer statement, FieldAccess fieldAccess) throws NotFoundException {
		if(!Modifier.isTransient(fieldAccess.getField().getModifiers())){
			String reemplaze = Tokens.DEFAULT_VALUE_ASSIGMENT.getRegExp();
			
			String newExpresion = 
				"($fieldTypeName) $interceptor.fieldWrite($this, '$fieldName', $argument1, $this.$fieldName);";
			statement.replace(0, statement.length(), StringUtils.replace(statement.toString(), reemplaze, newExpresion));
		}
	}

	@Override
	protected void modifyReaderFieldAccess(StringBuffer statement, FieldAccess fieldAccess) throws NotFoundException {
		String reemplaze = Tokens.DEFAULT_FIELD.getRegExp();
		
		String newExpresion = "($fieldTypeName) $interceptor.fieldRead($this, '$fieldName', $this.$fieldName);";
		
		statement.replace(0, statement.length(), StringUtils.replace(statement.toString(), reemplaze, newExpresion));
	}

	@Override
	protected String getSpecificPropertyKey() {
		return "TransactionFieldAccessInterceptor";
	}

}