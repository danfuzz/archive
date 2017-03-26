package com.milk.plastic.iface;

/**
 * Reference to a named field of a named module. It consists of three
 * things: a module name, a field name, and an expected type. The point is
 * that this object maintains symbolic information about interconnectivity,
 * which allows for out-of-order instantiation of modules and the
 * preservation of information useful for debugging a module network.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FieldRef
implements Ref
{
    /** non-null; the environment and module name parts of the reference */
    private NameRef myModuleRef;

    /** non-null; the name of the field of the module */
    private String myFieldName;

    /** non-null; the type that the resolved object should be */
    private Class myType;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance with a given module reference. In addition to
     * being used directly by the full public constructor, this is used to
     * implement {@link #withType}. It is a private constructor because we
     * can't count on outsiders to pass in an appropriately type-restricted
     * reference.
     *
     * @param moduleRef the module reference
     * @param fieldName non-null; the name of the field on the module
     * @param type non-null; the type that the resolved object should be 
     */
    private FieldRef (NameRef moduleRef, String fieldName, Class type)
    {
	if (fieldName == null)
	{
	    throw new IllegalArgumentException ("fieldName = null");
	}

	if (type == null)
	{
	    throw new IllegalArgumentException ("type = null");
	}

	myModuleRef = moduleRef;
	myFieldName = fieldName;
	myType = type;
    }

    /**
     * Construct an instance.
     *
     * @param env non-null; the environment to resolve in
     * @param moduleName non-null; the name of the module
     * @param fieldName non-null; the name of the field on the module
     * @param type non-null; the type that the resolved object should be
     */
    public FieldRef (Environment env, String moduleName, String fieldName, 
		     Class type)
    {
	this (new NameRef (env, moduleName, Module.class),
	      fieldName,
	      type);
    }

    /**
     * Construct an instance which has no type restriction.
     *
     * @param env non-null; the environment to resolve in
     * @param moduleName non-null; the name of the module
     * @param fieldName non-null; the name of the field on the module
     */
    public FieldRef (Environment env, String moduleName, String fieldName)
    {
	this (env, moduleName, fieldName, Object.class);
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Return the full string form of this object.
     *
     * @return the string form
     */
    public String toString ()
    {
	return "FieldRef[moduleRef=" + myModuleRef + "; fieldName=" + 
	    myFieldName + "; type: " + myType.getName () + "]";
    }

    // interface's comment suffices
    public Class getType ()
    {
	return myType;
    }

    // interface's comment suffices
    public Ref withType (Class type)
    {
	return new FieldRef (myModuleRef, myFieldName, type);
    }

    // interface's comment suffices
    public Object resolve ()
    {
	Module module = (Module) myModuleRef.resolve ();
	Object resolved = module.getField (myFieldName);

	if (! myType.isInstance (resolved))
	{
	    throw new PlasticException ("Incompatible type for field (" +
					getDebugString () +
					"); got " + 
					resolved.getClass ().getName ());
	}

	return resolved;
    }

    /**
     * Get a {@link NameRef} representing the module that this 
     * instance looks up.
     *
     * @return a reference to the module
     */
    public NameRef getModuleRef ()
    {
	return myModuleRef;
    }

    /**
     * Get a short string representing the value, in a form suitable
     * for humans.
     *
     * @return such a string
     */
    public String getDebugString ()
    {
	return myModuleRef.getName () + '.' + myFieldName + ": " +
	    myType.getName ();
    }
}

