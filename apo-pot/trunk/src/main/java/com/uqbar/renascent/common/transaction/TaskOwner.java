package com.uqbar.renascent.common.transaction;

import ar.com.andinasoftware.commons.NamedObject;

/**
 * Un objeto que puede ser dueño de una transaccion de objetos. (es decir, iniciarla, comitearla,
 * rollbackearla a piacere).
 * 
 * @author rgomez
 * @author jpicasso
 */
public interface TaskOwner extends NamedObject {
    
	/**
	 * 
	 * @return
	 */
    public boolean isTransactional();
    
    /**
     * 
     */
    public void setTransaction(ObjectTransaction transaction);

    /**
     * 
     * @return
     */
    public ObjectTransaction getTransaction();
    
}