package com.milk.plastic.util;

import com.milk.plastic.iface.Environment;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract implementation of the base functionality of a {@link Module}.
 * It handles keeping track of the associated factory, the initialization
 * arguments and the template, and knows how to initialize empty fields
 * and bind input ports.
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
abstract public class BaseModule
implements Module
{
    /** the factory which spawned this instance */
    private Factory myFactory;

    /** the full template for this instance */
    private Template myTemplate;

    /** the arguments which this instance was constructed from */
    private Map myArguments;

    /** array of field values, in the order given in the template */
    private Object[] myFields;

    /** map from names to objects bound by {@link #bind} */
    private HashMap myBoundObjects;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. Any field in the template that doesn't
     * have a corresponding argument in <code>args</code> is constructed
     * (via {@link Class#newInstance}).
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     * @exception PlasticException thrown if there was trouble instantiating
     * any of the blank fields
     */
    public BaseModule (Factory factory, Template template, Map args)
	throws PlasticException
    {
	myFactory = factory;
	myTemplate = template;
	myArguments = args;
	myBoundObjects = new HashMap ();

	// initialize the fields
	int count = template.getCount ();
	myFields = new Object[count];
	for (int i = 0; i < count; i++)
	{
	    String name = template.getName (i);
	    Object value = args.get (name);
	    if (value != null)
	    {
		// the field has an associated argument; set it based on
		// that argument
		myFields[i] = value;

		if (value instanceof Ref)
		{
		    // it's a reference, so add the name to the bound
		    // object map
		    myBoundObjects.put (name, null);
		}
	    }
	    else
	    {
		// the field has no associated argument; attempt to
		// initialize the field with a newInstance
		Class type = template.getType (i);
		try
		{
		    myFields[i] = type.newInstance ();
		}
		catch (Exception ex)
		{
		    throw new PlasticException ("Trouble initializing field "
						+ "(" + name + " of type " +
						type.getName () + ")",
						ex);
		}
	    }
	}
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    final public Factory getFactory ()
    {
	return myFactory;
    }

    // interface's comment suffices
    final public Template getTemplate ()
    {
	return myTemplate;
    }

    // interface's comment suffices
    final public Map getArguments ()
    {
	return Collections.unmodifiableMap (myArguments);
    }

    // interface's comment suffices
    final public Object getField (int n)
    {
	if ((n < 0) || (n >= myFields.length))
	{
	    throw new IllegalArgumentException ("n = " + n);
	}

	return myFields[n];
    }

    // interface's comment suffices
    final public Object getField (String name)
    {
	return myFields[myTemplate.indexOf (name)];
    }

    // interface's comment suffices
    final public void bind ()
    {
	// iterate over the entries in the bound objects map, resolving
	// each
	Iterator i = myBoundObjects.entrySet ().iterator ();
	while (i.hasNext ())
	{
	    Map.Entry one = (Map.Entry) i.next ();
	    String name = (String) one.getKey ();
	    Ref ref = (Ref) getField (name);
	    Object resolved = ref.resolve ();
	    one.setValue (resolved);
	}

	// now, provide the subclass an opportunity to do something
	// useful with all the above

	bind1 ();
    }

    // interface's comment suffices
    final public void reset ()
    {
	// simply clear out the values in the bound object map
	Iterator i = myBoundObjects.entrySet ().iterator ();
	while (i.hasNext ())
	{
	    Map.Entry one = (Map.Entry) i.next ();
	    one.setValue (null);
	}

	// now, provide the subclass an opportunity to do whatever
	// resetting it needs to do

	reset1 ();
    }

    // ------------------------------------------------------------------------
    // public methods that must be overridden by subclasses

    // part of interface
    abstract public void tick ();

    // ------------------------------------------------------------------------
    // protected methods that must be overridden by subclasses

    /**
     * Subclass's chance to perform <code>bind</code>-time operations.
     * This is called by {@link #bind} after the references have been
     * resolved, meaning that it is okay to call {@link #getBoundObject}
     * to do any needed internal setup.
     */
    abstract protected void bind1 ();

    /**
     * Subclass's chance to perform <code>reset</code>-time operations.
     * This is called by {@link #reset} after it does its own resetting.
     */
    abstract protected void reset1 ();

    // ------------------------------------------------------------------------
    // protected utility methods

    /**
     * Explicitly set a field. This will throw an exception if the new
     * value doesn't match the type restriction of the field.
     *
     * @param name the name of the field to set
     * @param value the value to set it to
     */
    protected final void setField (String name, Object value)
    {
	int index = myTemplate.indexOf (name);
	Class type = myTemplate.getType (index);

	if (! type.isInstance (value))
	{
	    throw new PlasticException ("Bad value for field (" + name + 
					"); expected " + type.getName () +
					" but got " + 
					value.getClass ().getName ());
	}

	myFields[index] = value;
    }

    /**
     * Get the bound object associated with the named field. When {@link
     * #bind} is called on this instance, it creates a map for each
     * template entry associated with a {@link Ref}, mapping the same name
     * to the result of calling {@link Ref#resolve()} on the reference in
     * question. This method hands out those resolved objects. Since this
     * method is relatively slow in the scheme of things, it is recommended
     * that subclasses only use this during one-time initialization,
     * caching the results as appropriate.
     *
     * @param name the name to look up
     * @return the resolved object associated with the given name 
     */
    protected final Object getBoundObject (String name)
    {
	if (name == null)
	{
	    throw new IllegalArgumentException ("name = null");
	}

	Object result = myBoundObjects.get (name);

	if (result == null)
	{
	    if (myBoundObjects.containsKey (name))
	    {
		throw new PlasticException ("Module not yet bound.");
	    }

	    throw new PlasticException ("No such bound name (" + name + 
					"); fields: " + myBoundObjects);
	}

	return result;
    }
}
