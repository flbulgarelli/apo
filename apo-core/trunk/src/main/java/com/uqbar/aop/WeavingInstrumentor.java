package com.uqbar.aop;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import com.uqbar.aop.javassit.parser.JavassistParser;
import com.uqbar.aop.javassit.parser.Tokens;

/**
 * Intruduce bytecode para que se pueda interceptar todos los fields del objeto.
 * 
 */
public class WeavingInstrumentor extends ExprEditor {
	/**
	 * Lista con FieldAccessInterceptors, para armar el codigo que se va a agregar a los accesors.
	 */
	// DESIGN-REFACTOR: que se inyecten por IOC
	private List<FieldAccessInterceptor> fieldAccessInterceptors = new ArrayList<FieldAccessInterceptor>();
	private Boolean aopEnabled;

	// ***************************
	// ** FieldAccess weaving
	// ***************************

	public void edit(FieldAccess fieldAccess) {
		if (this.mustWeave(fieldAccess)) {
			if (fieldAccess.isWriter()) {
				this.weaveFieldWrite(fieldAccess);
			}

			if (fieldAccess.isReader()) {
				this.weaveFieldRead(fieldAccess);
			}
		}
	}
	
	private void weaveFieldWrite(FieldAccess fieldAccess) {
		StringBuffer statement = new StringBuffer(Tokens.ORIGINAL_STATEMENT_ASSIGMENT.getRegExp());
		try {
			for (FieldAccessInterceptor interceptor : this.fieldAccessInterceptors) {
				interceptor.addYourselfToFieldWrite(statement, fieldAccess);
			}
			fieldAccess.replace(JavassistParser.parser(fieldAccess, statement.toString()));
		}
		catch (CannotCompileException exception) {
			throw getVerbosedException(exception, statement.toString(), fieldAccess);
		}
		catch (NotFoundException exception) {
			throw getVerbosedException(exception, statement.toString(), fieldAccess);
		}
	}

	/**
	 * @param fieldAccess
	 * @param name Name of the field being processed
	 */
	private void weaveFieldRead(FieldAccess fieldAccess) {
		StringBuffer statement = new StringBuffer(Tokens.ORIGINAL_STATEMENT_READER.getRegExp());
		try {
			for (FieldAccessInterceptor interceptor : this.fieldAccessInterceptors) {
				interceptor.addYourselfToFieldRead(statement, fieldAccess);
			}
			fieldAccess.replace(JavassistParser.parser(fieldAccess, statement.toString()));
		}
		catch (CannotCompileException exception) {
			throw this.getVerbosedException(exception, statement.toString(), fieldAccess);
		}
		catch (NotFoundException exception) {
			throw this.getVerbosedException(exception, statement.toString(), fieldAccess);
		}
	}

	/**
	 * Evita agregar el aspecto a los field access:
	 * <ul>
	 * <li>Estáticos.
	 * <li>Que ocurran dentro de un constructor.
	 * </ul>
	 */
	private boolean mustWeave(FieldAccess fieldAccess) {
		return !fieldAccess.isStatic() && //
			!fieldAccess.where().getMethodInfo().toString().startsWith("<init>") && this.isAopEnable();
	}

	private boolean isAopEnable() {
		if (aopEnabled == null) {
			aopEnabled = AopConfig.isAOPEnable();
		}
		return aopEnabled.booleanValue();
	}

	// ***************************
	// ** Exception helpers
	// ***************************
	private RuntimeException getVerbosedException(Exception ex, String javaStatement, FieldAccess fieldAccess) {
		return new RuntimeException(ex.getMessage() + "\ncontainerClassName:" + fieldAccess.getClassName()
			+ "\nlineNumber" + fieldAccess.getLineNumber() + "\ncontainerMethod" + fieldAccess.where().getMethodInfo()
			+ "\nJavassist Statement" + javaStatement, ex);
	}

	public WeavingInstrumentor addFieldAccessInterceptor(FieldAccessInterceptor fieldAccessInterceptor) {
		this.fieldAccessInterceptors.add(fieldAccessInterceptor);
		return this;
	}
}