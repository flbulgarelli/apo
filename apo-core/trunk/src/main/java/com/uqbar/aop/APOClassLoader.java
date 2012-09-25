package com.uqbar.aop;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

import com.uqbar.aop.AdviceWeaver;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * Nuestro classloader, que al cargar una clase, le hace weaving para meterle la magia de aspectos.
 * 
 * Parametro para correr tests con este ClassLoader.
 * 
 * -Djava.system.class.loader=com.uqbar.renascent.framework.aop.FrameworkClassLoader
 * 
 * @author nny
 * @author jfernandes
 * @author npasserini
 */
public abstract class APOClassLoader extends ClassLoader {
	private Vector<String> notDefinedHere; // must be atomic.
	private Vector<String> notDefinedPackages; // must be atomic.
	private ClassPool classPool;
	private AdviceWeaver adviceWeaver;

	// ***************************
	// ** Constructores
	// ***************************
	public APOClassLoader(ClassLoader parent) {
		this(parent, ClassPool.getDefault());
	}

	/**
	 * Creates a new class loader using the specified parent class loader for delegation.
	 * @param parent the parent class loader.
	 * @param cp the source of class files.
	 * @param adviceWeaverClass2 
	 */
	public APOClassLoader(ClassLoader parent, ClassPool cp ) {
		super(parent);
		this.init(cp);
	}

	private void init(ClassPool cp) {
		this.notDefinedHere = new Vector<String>();
		this.notDefinedHere.add("javassist.Loader");
		
		this.notDefinedPackages = new Vector<String>();
		this.notDefinedPackages.addAll(Arrays.asList(
			"java.", 
			"javax.", 
			"sun.", 
			"com.sun.", 
			"org.w3c.", 
			"org.xml.", 
			"org.apache.commons.logging", 
			"org.apache.log4j", 
			"org.apache.xerces", 
			"net.sf", 
			"com.jprofiler"));
		
		this.classPool = cp;
		this.adviceWeaver = createAdviceWeaver(cp);
		adviceWeaver.init();
	}

	protected abstract AdviceWeaver createAdviceWeaver(ClassPool cp);

	// ***************************
	// ** Metodos
	// ***************************

	/**
	 * Loads a class and calls <code>main()</code> in that class.
	 * @param args command line parameters.
	 *            <ul>
	 *            <code>args[0]</code> is the class name to be loaded. <br>
	 *            <code>args[1..n]</code> are parameters passed to the target <code>main()</code>.
	 *            </ul>
	 */
	public void run(String[] args) throws Throwable {
		int n = args.length - 1;
		if (n >= 0) {
			String[] args2 = new String[n];
			for (int i = 0; i < n; ++i) {
				args2[i] = args[i + 1];
			}
			this.run(args[0], args2);
		}
	}

	/**
	 * Loads a class and calls <code>main()</code> in that class.
	 * 
	 * @param classname the loaded class.
	 * @param args parameters passed to <code>main()</code>.
	 */
	public void run(String classname, String[] args) throws Throwable {
		try {
			this.loadClass(classname).getDeclaredMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { args });
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	/**
	 * Requests the class loader to load a class.
	 */
	protected Class<?> loadClass(String name, boolean resolve) throws ClassFormatError, ClassNotFoundException {
		Class<?> c = this.findLoadedClass(name);
		if (c == null) {
			c = this.loadClassByDelegation(name);
		}
		if (c == null) {
			c = this.findClass(name);
		}
		if (c == null) {
			c = this.delegateToParent(name);
		}
		if (resolve) {
			this.resolveClass(c);
		}
		return c;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}

	/**
	 * Finds the specified class using <code>ClassPath</code>. If the source throws an exception, this returns
	 * null.
	 * 
	 * <p>
	 * This method can be overridden by a subclass of <code>Loader</code>. Note that the overridden method
	 * must not throw an exception when it just fails to find a class file.
	 * 
	 * @return null if the specified class could not be found.
	 * @throws ClassNotFoundException if an exception is thrown while obtaining a class file.
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] classfile;
		try {
			if (this.classPool != null) {
				try {
					CtClass ctClass = this.classPool.get(name);
					this.adviceWeaver.applyAdvice(ctClass);
					classfile = ctClass.toBytecode();
				}
				catch (NotFoundException e) {
					return null;
				}
			}
			else {
				String jarname = "/" + name.replace('.', '/') + ".class";
				InputStream in = this.getClass().getResourceAsStream(jarname);
				if (in == null) {
					return null;
				}

				// esta clase tiene scope default
				// classfile = ClassPoolTail.readStream(in);
				classfile = null;
			}
		}
		catch (Exception exception) {
			exception.printStackTrace(System.err);
			throw new ClassNotFoundException("caught an exception while obtaining a class file for " + name, exception);
		}

		int i = name.lastIndexOf('.');
		if (i != -1) {
			String pname = name.substring(0, i);
			if (this.getPackage(pname) == null) {
				try {
					this.definePackage(pname, null, null, null, null, null, null, null);
				}
				catch (IllegalArgumentException e) {
					// ignore. maybe the package object for the same
					// name has been created just right away.
				}
			}
		}

		return this.defineClass(name, classfile, 0, classfile.length);
	}

	/**
	 * The swing components must be loaded by a system class loader. javax.swing.UIManager loads a (concrete)
	 * subclass of LookAndFeel by a system class loader and cast an instance of the class to LookAndFeel for
	 * (maybe) a security reason. To avoid failure of type conversion, LookAndFeel must not be loaded by this
	 * class loader.
	 * 
	 * <b>java., javax.</b> (and probably <b>sun. and com.sun</b>) can't be loaded with a custom classloader
	 * because the security manager throws a java.lang.SecurityException.
	 */
	protected Class<?> loadClassByDelegation(String name) throws ClassNotFoundException {
		return this.mustDelegate(name) ? this.delegateToParent(name) : null;
	}

	private boolean mustDelegate(String name) {
		if (this.notDefinedHere.contains(name)) {
			return true;
		}
		int n = this.notDefinedPackages.size();
		for (int i = 0; i < n; ++i) {
			if (name.startsWith((String) this.notDefinedPackages.elementAt(i))) {
				return true;
			}
		}
		return false;
	}

	protected Class<?> delegateToParent(String classname) throws ClassNotFoundException {
		ClassLoader cl = this.getParent();
		return cl != null ? cl.loadClass(classname) : this.findSystemClass(classname);
	}

}