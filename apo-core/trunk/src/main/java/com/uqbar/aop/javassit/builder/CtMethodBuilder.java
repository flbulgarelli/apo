package com.uqbar.aop.javassit.builder;

import com.uqbar.aop.javassit.parser.JavassitParser;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class CtMethodBuilder {

	private String methodName;
	private int modifier;
	private CtClass returnType;
	private CtClass owner;
	private String body;
	private CtClass[] parametersType;

	public CtMethodBuilder withName(String name) {
		this.methodName = name;
		return this;
	}

	public CtMethodBuilder withModifier(int modifier) {
		this.modifier = modifier;
		return this;
	}

	public CtMethodBuilder withReturnType(CtClass returnType) {
		this.returnType = returnType;
		return this;
	}

	public CtMethodBuilder withOwner(CtClass owner) {
		this.owner = owner;
		return this;
	}

	public CtMethodBuilder witBody(String body) {
		this.body = body;
		return this;
	}

	public CtMethodBuilder withparameterClass(CtClass... parametersType) {
		this.parametersType = parametersType;
		return this;
	}
	
	public CtMethod build(){
		return createMethod(methodName, modifier, returnType, owner, body, parametersType);
	}
	
	private CtMethod createMethod(String methodName, int modifier, CtClass returnType, CtClass owner,
			String body, CtClass... parametersType)  {

		try {
			CtMethod method = CtNewMethod.make(
			         modifier,
			         returnType, 
			         methodName,//name
			         parametersType, //parameters type
			         new CtClass[]{},//exceptions
			         JavassitParser.parser(body),
			         owner);
			return method;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
