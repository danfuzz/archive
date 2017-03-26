package com.milk.plastic.iface;

/**
 * Reference to a name, which may be resolved in a given environment.
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
final public class NameRef
implements Ref
{
    /** non-null; the environment to resolve in */
    private Environment myEnvironment;

    /** non-null; the name */
    private String myName;

    /** non-null; the type that the resolved object should be */
    private Class myType;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param env non-null; the environment to resolve in
     * @param name non-null; the name
     * @param type non-null; the type that the resolved object should be
     */
    public NameRef (Environment env, String name, Class type)
    {
	if (env == null)
	{
	    throw new IllegalArgumentException ("env = null");
	}

	if (name == null)
	{
	    throw new IllegalArgumentException ("name = null");
	}

	if (type == null)
	{
	    throw new IllegalArgumentException ("type = null");
	}

	myEnvironment = env;
	myName = name;
	myType = type;
    }

    /**
     * Construct an instance which has no type restriction.
     *
     * @param env non-null; the environment to resolve in
     * @param name non-null; the name
     */
    public NameRef (Environment env, String name)
    {
	this (env, name, Object.class);
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
	return "NameRef[environment=" + myEnvironment + "; name=" + 
	    myName + "; type: " + myType.getName () + "]";
    }

    /**
     * Get the name this instance refers to.
     *
     * @return the name
     */
    public String getName ()
    {
	return myName;
    }

    // interface's comment suffices
    public Class getType ()
    {
	return myType;
    }

    // interface's comment suffices
    public Ref withType (Class type)
    {
	return new NameRef (myEnvironment, myName, type);
    }

    // interface's comment suffices
    public Object resolve ()
    {
	Object o = myEnvironment.get (myName);

	if (myType.isInstance (o))
	{
	    return o;
	}

	throw new PlasticException ("Incompatible type for object (" +
				    myName + "); is " + 
				    o.getClass ().getName () + 
				    " but expected " + myType);
    }
}
