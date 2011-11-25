package com.uqbar.renascent.framework.aop;

import javassist.NotFoundException;
import javassist.expr.FieldAccess;

/**
 * @author npasserini
 * @author jfernandes
 */
public abstract class FieldAccessInterceptor {
	private Boolean enabled;

	public FieldAccessInterceptor() {
		super();
	}

	protected abstract void modifyWriterFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess)
			throws NotFoundException;

	protected abstract void modifyReaderFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess)
			throws NotFoundException;

	public final void addYourselfToFieldWrite(StringBuffer statement, FieldAccess fieldAccess) throws NotFoundException {
		if (this.isEnabled()) {
			this.modifyWriterFieldAccess(statement, fieldAccess);
		}
	}

	public final void addYourselfToFieldRead(StringBuffer statement, FieldAccess fieldAccess) throws NotFoundException {
		if (this.isEnabled()) {
			this.modifyReaderFieldAccess(statement, fieldAccess);
		}
	}

	protected boolean isEnabled() {
		if (this.enabled == null) {
			this.enabled = AopConfig.isEnable(this.getSpecificPropertyKey());
		}
		return this.enabled.booleanValue();
	}

	/**
	 * Utilizado para armar la key de habilitación en el framework-config.properties
	 */
	protected abstract String getSpecificPropertyKey();

}
