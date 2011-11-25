package com.uqbar.poo.aop;

import java.beans.PropertyChangeListener;

import com.uqbar.aop.javassit.builder.CtMethodBuilder;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * @author nnydjesus
 *
 */
public class ObservableBehavior {



	public void addBehavior(CtClass ctClass) {
		addFieldChangeSupport(ctClass);
		addGeterChangeSupport(ctClass);
		addFirePropertyChangeMethod(ctClass);
		addAddPropertyChangeListenerMethod(ctClass);
		addRemovePropertyChangeListenerMethod(ctClass);
	}
	
	/**
	 * Agrega el field changeSupport para el maejo de eventos 
	 * @param ctClass
	 */
	private void addFieldChangeSupport(CtClass ctClass) {
		
		this.addField(ctClass, MyPropertyChangeSupport.class,
				"changeSupport", Modifier.TRANSIENT);
	}

	/**
	 * Metodo que permite que un observador se registre a un evento
	 * @param ctClassOwner
	 */
	private void addAddPropertyChangeListenerMethod(CtClass ctClassOwner) {
		CtClass ctClsString = getClass(String.class, ctClassOwner);
		CtClass ctPropertyChangeListener = getClass(PropertyChangeListener.class, ctClassOwner);
		
		String addPropertyChangeListenerBody =	"{" +
				"$this.getChangeSupport().addPropertyChangeListener($argument1, $argument2);}";
		
		CtMethod method = new CtMethodBuilder()
			.withName("addPropertyChangeListener")
			.withModifier(Modifier.PUBLIC)
			.withReturnType(CtClass.voidType)
			.withOwner(ctClassOwner)
			.witBody(addPropertyChangeListenerBody)
			.withparameterClass(ctClsString, ctPropertyChangeListener)
			.build();
		
		addMethod(ctClassOwner, method);
		
	}

	private void addRemovePropertyChangeListenerMethod(CtClass ctClassOwner) {
		CtClass ctClsString = getClass(String.class, ctClassOwner);
		CtClass ctPropertyChangeListener = getClass(PropertyChangeListener.class, ctClassOwner);
		
		String removePropertyChangeListenerBody =	"{" +
				"$this.getChangeSupport().removePropertyChangeListener($argument1, $argument2);}"; 
		
		CtMethod method = new CtMethodBuilder()
			.withName("removePropertyChangeListener")
			.withModifier(Modifier.PUBLIC)
			.withReturnType(CtClass.voidType)
			.withOwner(ctClassOwner)
			.witBody(removePropertyChangeListenerBody)
			.withparameterClass(ctClsString, ctPropertyChangeListener)
			.build();
		
		addMethod(ctClassOwner, method);

		
	}

	/**
	 * Metodo que avisa a los observadores que se cambio una propiedad
	 * @param ctClassOwner
	 */
	private void addFirePropertyChangeMethod(CtClass ctClassOwner) {
		CtClass ctClsString = getClass(String.class, ctClassOwner);
		CtClass ctClsObject = getClass(Object.class, ctClassOwner);
		
		String firePropertyChangeBody =	"{" +
				"$this.getChangeSupport().firePropertyChange($argument1, $argument2, $argument3);}";
		
		CtClass[] parametersType = new CtClass[]{ctClsString, ctClsObject, ctClsObject};
		
		CtMethod method = new CtMethodBuilder()
			.withName("firePropertyChange")
			.withModifier(Modifier.PUBLIC)
			.withReturnType(CtClass.voidType)
			.withOwner(ctClassOwner)
			.witBody(firePropertyChangeBody)
			.withparameterClass(parametersType)
			.build();
	
		addMethod(ctClassOwner, method);

	}

	/**
	 * Geter para changeSupport, que instancia la variable si no es nula
	 * @param ctClassOwner
	 */
	private void addGeterChangeSupport(CtClass ctClassOwner) {
		CtClass changeSuportType = getClass(MyPropertyChangeSupport.class, ctClassOwner);
		
		ctClassOwner.getClassPool().importPackage(MyPropertyChangeSupport.class.getPackage().getName());
		
		String getChangeSupportBody = 
			"{" +
				"if($this.changeSupport == null) {"
					+ " $this.changeSupport = new MyPropertyChangeSupport($this);}"+
			    "return $this.changeSupport;" +
			"}";
		
		CtMethod method = new CtMethodBuilder()
			.withName("getChangeSupport")
			.withModifier(Modifier.PUBLIC)
			.withReturnType(changeSuportType)
			.withOwner(ctClassOwner)
			.witBody(getChangeSupportBody)
			.build();

		addMethod(ctClassOwner, method);
		
	}

	
	//HELPPERS
		
	private void addMethod(CtClass owner, CtMethod method)  {

		try {
			owner.addMethod(method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public CtClass getClass(Class aClass, CtClass ctClass){
		return getClass(aClass.getName(), ctClass);
		
	}
	
	public CtClass getClass(String className, CtClass ctClass){
		try {
			return ctClass.getClassPool().get(className);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void addField(CtClass owner, Class fieldClass, String name,
			int modifier) {
		
		try {
			CtField ctField = new CtField(getClass(fieldClass.getName(), owner), name, owner);
			ctField.setModifiers(modifier);
			owner.addField(ctField);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
}

