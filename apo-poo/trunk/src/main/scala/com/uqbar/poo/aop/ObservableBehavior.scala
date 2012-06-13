package com.uqbar.poo.aop;

import java.beans.PropertyChangeListener;
import com.uqbar.aop.javassit.builder.CtMethodBuilder;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * @author nnydjesus
 *
 */
class ObservableBehavior {

  def addBehavior(ctClass: CtClass) {
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
  private def addFieldChangeSupport(ctClass: CtClass) {
    val fieldName = "changeSupport";
    try {
      ctClass.getField(fieldName);
    } catch {
      case e: NotFoundException => {
        this.addField(ctClass, classOf[MyPropertyChangeSupport],
          fieldName, Modifier.TRANSIENT);
      }
    }
  }

  /**
   * Metodo que permite que un observador se registre a un evento
   * @param ctClassOwner
   */
  private def addAddPropertyChangeListenerMethod(ctClassOwner: CtClass) {
    var ctClsString = getClass(classOf[String], ctClassOwner)
    var ctPropertyChangeListener = getClass(classOf[PropertyChangeListener], ctClassOwner)

    val addPropertyChangeListenerBody = "{$this.getChangeSupport().addPropertyChangeListener($argument1, $argument2);}";

    var method = new CtMethodBuilder() {
      withName("addPropertyChangeListener")
      withModifier(Modifier.PUBLIC)
      withReturnType(CtClass.voidType)
      withOwner(ctClassOwner)
      witBody(addPropertyChangeListenerBody)
      withparameterClass(ctClsString, ctPropertyChangeListener)
    }.build()

    addMethod(ctClassOwner, method);

  }

  private def addRemovePropertyChangeListenerMethod(ctClassOwner: CtClass) {
    var ctClsString = getClass(classOf[String], ctClassOwner);
    var ctPropertyChangeListener = getClass(classOf[PropertyChangeListener], ctClassOwner);

    val removePropertyChangeListenerBody = "{ $this.getChangeSupport().removePropertyChangeListener($argument1, $argument2);}";

    var method = new CtMethodBuilder() {
      withName("removePropertyChangeListener")
      withModifier(Modifier.PUBLIC)
      withReturnType(CtClass.voidType)
      withOwner(ctClassOwner)
      witBody(removePropertyChangeListenerBody)
      withparameterClass(ctClsString, ctPropertyChangeListener)
    }.build()

    addMethod(ctClassOwner, method);

  }

  /**
   * Metodo que avisa a los observadores que se cambio una propiedad
   * @param ctClassOwner
   */
  private def addFirePropertyChangeMethod(ctClassOwner: CtClass) {
    val ctClsString = getClass(classOf[String], ctClassOwner);
    val ctClsObject = getClass(classOf[Object], ctClassOwner);

    var firePropertyChangeBody = "{" +
      "$this.getChangeSupport().firePropertyChange($argument1, $argument2, $argument3);}";

    var method = new CtMethodBuilder() {
      withName("firePropertyChange")
      withModifier(Modifier.PUBLIC)
      withReturnType(CtClass.voidType)
      withOwner(ctClassOwner)
      witBody(firePropertyChangeBody)
      withparameterClass(ctClsString, ctClsObject, ctClsObject)
    }.build()

    addMethod(ctClassOwner, method);

  }

  /**
   * Geter para changeSupport, que instancia la variable si no es nula
   * @param ctClassOwner
   */
  private def addGeterChangeSupport(ctClassOwner: CtClass) {
    val changeSuportType = getClass(classOf[MyPropertyChangeSupport], ctClassOwner);

    ctClassOwner.getClassPool().importPackage(classOf[MyPropertyChangeSupport].getPackage().getName());

    val getChangeSupportBody =
      "{"+
   		  "if($this.changeSupport == null) {"+
  		  	" $this.changeSupport = new MyPropertyChangeSupport($this);" +
          "}"+
     	 "return $this.changeSupport;"+
      "}"

    val method = new CtMethodBuilder() {
      withName("getChangeSupport")
      withModifier(Modifier.PUBLIC)
      withReturnType(changeSuportType)
      withOwner(ctClassOwner)
      witBody(getChangeSupportBody)
    }.build()

    addMethod(ctClassOwner, method);

  }

  //HELPPERS

  private def addMethod(owner: CtClass, method: CtMethod) {
    if (!owner.getMethods().contains(method)) {
      owner.addMethod(method);
    }
  }

  def getClass(aClass: Class[_], ctClass: CtClass): CtClass = getClass(aClass.getName(), ctClass)

  def getClass(className: String, ctClass: CtClass) = ctClass.getClassPool().get(className);

  private def addField(owner: CtClass, fieldClass: Class[_], name: String, modifier: Int) {
    var ctField = new CtField(getClass(fieldClass.getName(), owner), name, owner);
    ctField.setModifiers(modifier);
    if (!owner.getFields().contains(ctField)) {
      owner.addField(ctField);
    }

  }

}

