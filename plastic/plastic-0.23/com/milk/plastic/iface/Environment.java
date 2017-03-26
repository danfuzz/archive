package com.milk.plastic.iface;

import java.util.HashMap;

/**
 * A global environment containing bindings from names to values. An
 * environment is typically initialized to have bindings for a bunch of
 * {@link Factory} objects, but the act of loading a file typically causes
 * bindings for objects instantiated through factories to be created. Once
 * a name has been bound in an environment it may not be rebound to
 * something else.
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
final public class Environment
{
    /** map of bindings from names to values */
    private HashMap myBindings;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance whose bindings are initially empty.
     */
    public Environment ()
    {
	myBindings = new HashMap (50);
    }

    /**
     * Construct an instance whose bindings are inherited from the given
     * parent environment. The parent's bindings are copied; if
     * subsequently the parent's bindings change, then this instance will
     * <i>not</i> see those changes.
     *
     * @param parent the environment to inherit the initial bindings
     * from 
     */
    public Environment (Environment parent)
    {
	synchronized (parent.myBindings)
	{
	    myBindings = (HashMap) parent.myBindings.clone ();
	}
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Bind the given name to the given value. It is an error if the
     * name is already bound.
     *
     * @param name the name to bind
     * @param value the value to bind it to
     * @exception PlasticException thrown if the given name is
     * already bound in this instance
     */
    public void bind (String name, Object value)
    {
	if (name == null)
	{
	    throw new IllegalArgumentException ("name = null");
	}

	if (value == null)
	{
	    throw new IllegalArgumentException ("value = null");
	}

	synchronized (myBindings)
	{
	    if (myBindings.containsKey (name))
	    {
		throw new PlasticException ("Name (" + name + 
					    ") already bound in environment");
	    }

	    myBindings.put (name, value);
	}
    }

    /**
     * Get the binding for the given name
     *
     * @param name the name to look up
     * @return the value associated with that name
     * @exception PlasticException thrown if the name has no binding
     */
    public Object get (String name)
    {
	if (name == null)
	{
	    throw new IllegalArgumentException ("name = null");
	}

	synchronized (myBindings)
	{
	    Object result = myBindings.get (name);

	    if (result != null)
	    {
		return result;
	    }
	}

	throw new PlasticException ("Name (" + name + ") not bound.");
    }

    /**
     * Return <code>true</code> if the given name is bound, <code>false</code>
     * otherwise.
     *
     * @param name the name to look up
     * @return <code>true</code> if the name is bound
     */
    public boolean isBound (String name)
    {
	if (name == null)
	{
	    throw new IllegalArgumentException ("name = null");
	}

	synchronized (myBindings)
	{
	    if (myBindings.containsKey (name))
	    {
		return true;
	    }
	}
	
	return false;
    }
}
