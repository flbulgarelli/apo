package com.uqbar.renascent.framework.aop.transaction;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.uqbar.commons.utils.ReflectionUtils;

import com.uqbar.commons.collections.CollectionFactory;
import com.uqbar.commons.exceptions.ApplicationException;
import com.uqbar.commons.exceptions.ProgramException;
import com.uqbar.renascent.common.transaction.ObjectTransaction;
import com.uqbar.renascent.common.transaction.TaskOwner;

/**
 * {@link ObjectTransaction} default implementation.
 * Stores all the state specific for this transaction. That means all the field changed while executing
 * the domain logic with this transaction as part of the current context.
 * 
 * Weaving the domain object allows us to redirect all the field reading and writting behavior
 * to be redirected to this class, instead of directly accessing the state of the object.
 * 
 * This allow us to have isolation for each transaction in terms of object's state.
 * 
 * @author jfernandes
 * @author npasserini
 */
public class ObjectTransactionImpl implements ObjectTransaction {
    public static final String STATE_ROLLBACKED = "ROLLBACKED";
	public static final String STATE_COMMITED = "COMMITED";
	public static final String STATE_STARTED = "STARTED";
	public static final String STATE_NEW = "NEW";
	private static Log logger = LogFactory.getLog(ObjectTransaction.class);
    private ObjectTransactionImpl parent;
    
    /** Holds the state (fields) for all objects belonging to (and modified during) this transaction  */
    // TODO: use a java.util.IdentityHashMap instance instead of this
    private Map<IdentityWrapper, Map<String, Object>> attributeMap = CollectionFactory.createMap();
    private TaskOwner owner;
    private Long id;
    private String state;

    public ObjectTransactionImpl(ObjectTransactionImpl parent, TaskOwner owner, Long id) {
    	this.parent = parent;
    	this.owner = owner;
    	this.id = id;
    	this.state = STATE_NEW;
    }

    // ******************************
    // ** Simple Fields Interception
    // ** each weaved object will call this methods every time the execution flow changes one of
    // ** their fields.
    // ******************************
    
    public boolean fieldWrite(Object owner, String fieldName, boolean newValue, boolean oldValue) {
    	return ((Boolean) this.fieldWrite(owner, fieldName, Boolean.valueOf(newValue), Boolean.valueOf(oldValue))).booleanValue();
    }

    /**
     * Stores the new value in the transaction's map associated to the target object.
     * If the object is not in the map already, then it'll put it the first time.
     */
    public Object fieldWrite(Object owner, String fieldName, Object newValue, Object oldValue) {
    	this.getValueMap(IdentityWrapper.wrapKey(owner)).put(fieldName, newValue);
    	return oldValue;
    }

    public boolean fieldRead(Object owner, String fieldName, boolean value) {
    	return ((Boolean) this.fieldRead(owner, fieldName, Boolean.valueOf(value))).booleanValue();
    }
    
    /**
     * Checks whether the value is in the transaction's map (meaning it was modified during this transaction).
     * If that's the case, then it will return that value.
     * Otherwise, it delegates the value retrieving to its parent transaction (that will do the same).
     */
    public Object fieldRead(Object owner, String fieldName, Object value) {
    	Map<String, Object> valueMap = this.getValueMap(IdentityWrapper.wrapKey(owner), false);
    	if (valueMap != null && valueMap.containsKey(fieldName)) {
    		return valueMap.get(fieldName);
    	}
    	if (this.parent != null) {
    		return this.parent.fieldRead(owner, fieldName, value);
    	}
     	return value;
    }

    public Object arrayRead(Object owner, String fieldName, Object value) {
    	Map<String, Object> valueMap = this.getValueMap(IdentityWrapper.wrapKey(owner), true);
    	if (valueMap.containsKey(fieldName)) {
    		return valueMap.get(fieldName);
    	}
    	else {
    		Object[] oldData;
    		if (this.parent == null) {
    			oldData = (Object[]) value;
    		}
    		else {
    			oldData = (Object[]) this.parent.arrayRead(owner, fieldName, value);
    		}
    		
    		Object[] elementData = new Object[oldData.length];
    		System.arraycopy(oldData, 0, elementData, 0, oldData.length);
    		valueMap.put(fieldName, elementData);
    		return elementData;
    	}
    }

    // ***************************
    // ** Transaction LifeCycle
    // ***************************
    
    /**
     * Commits all changes to the parent transaction, if it does exists, or directly to the objects if it doesn't.
     * Also it changes to an "Commited" state.
     * @return the parent transaction or null if it hasn't one.
     */
    protected ObjectTransaction commit() {
    	// TODO: use Polymorphism: NullObjectTransaction should be responsable of commiting to objects.
    	if (this.parent != null && !(this.parent instanceof NullObjectTransaction)) {
    		this.parent.commitMap(this.attributeMap);
    	}
    	else {
    		this.populateValuesToObjects();
    	}
    	this.getLogger().debug("Commiting Object Transaction id[" + this.getId() + "]");
    	return this.transitionateToParentTransaction(STATE_COMMITED);
    }

    /**
     * @return
     */
    protected ObjectTransaction removeParent() {
    	ObjectTransaction transaction = this.parent;
    	this.parent = null;
    	return transaction;
    }

    /**
     * Ends this transaction wasting all changes it might have.
     * @return the parent transaction.
     */
    protected ObjectTransaction rollback() {
    	this.getLogger().debug("Rollbacking Object Transaction id[" + this.getId() + "]");
    	return this.transitionateToParentTransaction(STATE_ROLLBACKED);
    }

    private ObjectTransaction transitionateToParentTransaction(String newState) {
    	ObjectTransaction nextTransaction = this.removeParent();
    	this.getLogger().debug("Next Transaction: [" + (nextTransaction != null ? nextTransaction.getId() : null) + "]");
    	this.state = newState;
    	return nextTransaction;
    }

    /**
     * Commits the given changeset received as parameter into this transaction's current state.
     * This is used by a child transaction that is commited and therefore needs to commit all of its changed
     * into the parent transaction.
     */
    protected void commitMap(Map<IdentityWrapper, Map<String, Object>> attributeMap) {
    	for (Entry<IdentityWrapper, Map<String, Object>> entry : attributeMap.entrySet()) {
    		this.addValues((IdentityWrapper) entry.getKey(), entry.getValue());
    	}
    }

    /**
     * Commits all the changes to the real objects
     */
    protected void populateValuesToObjects() {
    	for (IdentityWrapper keyWrapper : this.attributeMap.keySet()) {
    		synchronized (keyWrapper.getKey()) {
    			for (Entry<String, Object> entry : this.getValueMap(keyWrapper).entrySet()) {
    				try {
//    					RefactormeUtils.forceFieldValue(keyWrapper.getKey(), entry.getKey(), entry.getValue());
    					ReflectionUtils.invokeSetter(keyWrapper.getKey(), entry.getKey(), entry.getValue());
    				}
    				catch (Exception exception) {
    					throw ((ProgramException) ApplicationException.unchecked(exception)) //
    						.addInfo("Object", keyWrapper.getKey()) //
    						.addInfo("Field", entry.getKey()) //
    						.addInfo("Value", entry.getValue()); 
    				}
    			}
    		}
    	}
   	}

    // ***************************
    // ** Private helpers
    // ***************************

    private void addValues(IdentityWrapper key, Map<String, Object> valuesMap) {
    	Map<String, Object> myValueMap = this.getValueMap(key);
    	for (Entry<String, Object> entry : valuesMap.entrySet()) {
    		myValueMap.put(entry.getKey(), entry.getValue());
    	}
    }

    private Map<String, Object> getValueMap(IdentityWrapper key) {
    	return this.getValueMap(key, true);
    }

    private Map<String, Object> getValueMap(IdentityWrapper key, boolean create) {
    	Map<String, Object> valueMap = this.attributeMap.get(key);
    	if (valueMap == null && create) {
    		valueMap = CollectionFactory.createMap();
    		this.attributeMap.put(key, valueMap);
    	}
    	return valueMap;
    }

    // ***************************
    // ** #Object
    // ***************************
    
    public String toString() {
    	return this.getClass().getSimpleName() + ": " + this.id + " state: " + this.state + " owner: " + this.owner + " parent: " + this.parent;
    }

    /**
     * Transactional clone implementation.
     * 
     * Implementacion del clone transaccional. Aca no solo hay que clonar el mapa de la transaccion actual,
     * sino que el nuevo mapa debe ser la union de los mapas de atributos del objeto sourceObject en todas las
     * transacciones.
     * 
     * @see ObjectTransactionManager.cloneAttributes()
     */
    public void cloneAttributes(Object sourceObject, Object targetObject) {
    	// copio los atributos transaccionales del objeto original al objeto clonado.
    	this.attributeMap.put(IdentityWrapper.wrapKey(targetObject), this.getAllValueMaps(IdentityWrapper.wrapKey(sourceObject)));
    }

    /**
     * Recolecta y une todos los mapas de todas las transacciones para la clave sourceKey.
     * 
     * @param sourceKey la clave del objeto del cual se desea obtener los atributos.
     * @return la union de todos los mapas de las transacciones donde este objeto figura.
     */
    private Map<String, Object> getAllValueMaps(IdentityWrapper sourceKey) {
    	Map<String, Object> unionMap = CollectionFactory.createMap();
    	unionMap.putAll(this.getValueMap(sourceKey));
    	if (this.parent != null) {
    		unionMap.putAll(this.parent.getAllValueMaps(sourceKey));
    	}
    	return unionMap;
    }
    
    // ***************************
    // ** Accessors
    // ***************************
    
    public ObjectTransaction getParent() {
    	return this.parent;
    }

    public TaskOwner getOwner() {
    	return this.owner;
    }

    public Long getId() {
    	return this.id;
    }

    protected Log getLogger() {
    	return logger;
    }

    // for tx monitor only
    public Map<IdentityWrapper, Map<String, Object>> getAttributeMap() {
    	return this.attributeMap;
    }

    public void setState(String newState) {
    	this.state = newState;
    }

    public String getState() {
    	return this.state;
    }
    
}