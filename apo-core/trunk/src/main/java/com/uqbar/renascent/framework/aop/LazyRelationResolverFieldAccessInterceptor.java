package com.uqbar.renascent.framework.aop;

import javassist.NotFoundException;
import javassist.expr.FieldAccess;

/**
 * Agrega la llamada al metodo correspondiente de la objectReference, para hidratar el objeto
 * 
 * @author jpicasso
 * @author lgassman
 */
public class LazyRelationResolverFieldAccessInterceptor extends FieldAccessInterceptor {

    /**
     * No se puede interceptar las llamadas a la object reference (proque entra en loop), ni a persitorReference
     * (porque se pide mientras se construye la ObjectReference) ni a isDirty porque no queremos que el objeto se
     * hidrate al preguntar eso
     */
    private boolean mustWeave(FieldAccess fieldAccess) {
	return !(fieldAccess.getFieldName().equals("objectReference") ||
		fieldAccess.getFieldName().equals("isDirty") ||
		fieldAccess.where().getMethodInfo().getName().equals("getObjectReference") ||
		fieldAccess.getFieldName().equals("persistorReference") ||
		fieldAccess.where().getMethodInfo().getName().equals("getPersistorReference") ||
		fieldAccess.getFieldName().equals("proxiedPartition") || 
		fieldAccess.where().getMethodInfo().getName().equals("getProxiedPartition") || 
		fieldAccess.getFieldName().equals("persistible") || 
		fieldAccess.where().getMethodInfo().getName().equals("getPersistible"));
    }

    protected void modifyWriterFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess) throws NotFoundException {
    	writerStatement.insert(0, mustWeave(fieldAccess) ? "$0.getObjectReference().fieldAccessed();" : "");
    }

    protected void modifyReaderFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess) throws NotFoundException {
    	this.modifyWriterFieldAccess(writerStatement, fieldAccess);
    }

    protected String getSpecificPropertyKey() {
    	return "LazyRelationResolverFieldAccessInterceptor";
    }
    
}