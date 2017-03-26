package com.milk.plastic.util;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation of the base functionality of a {@link Factory}.
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
abstract public class BaseFactory
implements Factory
{
    /** array of classes to use for getting constructors;
     * @see #getStandardConstructor */
    private static final Class[] CONSTRUCTOR_ARGS =
	new Class[] { Factory.class, Template.class, Map.class };

    /** the short name */
    private String myShortName;

    /** the base instantiation template */
    private Template myTemplate;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param shortName the short name
     * @param template the base instantiation template
     */
    public BaseFactory (String shortName, Template template)
    {
	myShortName = shortName;
	myTemplate = template;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    final public String getShortName ()
    {
	return myShortName;
    }

    // superclass's comment suffices
    final public Template getFactoryTemplate ()
    {
	return myTemplate;
    }

    // superclass's comment suffices
    public Template getInstantiationTemplate (Map args)
	throws PlasticException
    {
	// can't trust the outside world not to mess around with the
	// map
	args = new HashMap (args);

	// we assume that the subclass behaves normally and that it's
	// safe to merely remove entries that correspond to outputs
	Template t = getObjectTemplate (args);
	return t.withNoPorts ();
    }

    // superclass's comment suffices
    final public Template getObjectTemplate (Map args)
	throws PlasticException
    {
	// can't trust the outside world not to mess around with the
	// map
	args = new HashMap (args);

	myTemplate.checkArgsAllowExtras (args);
	return getObjectTemplate1 (args);
    }

    // superclass's comment suffices
    final public Object make (Map args)
	throws PlasticException
    {
	// can't trust the outside world not to mess around with the
	// map
	args = new HashMap (args);

	// we assume that the subclass behaves normally and that it's
	// safe to merely remove entries that correspond to outputs
	// to derive the instantiation template from the full object
	// template

	myTemplate.checkArgsAllowExtras (args);
	Template full = getObjectTemplate1 (args);
	Template inst = full.withNoPorts ();
	inst.checkArgsNoExtras (args);
	return make1 (full, args);
    }

    // ------------------------------------------------------------------------
    // protected methods that must be overridden by subclasses

    /**
     * Do the actual processing for {@link #getObjectTemplate} and {@link
     * #getInstantiationTemplate}. By the time this is called, the argument
     * map has already been typechecked against the template, so there will
     * be no missing arguments, and raw casts that conform to the template
     * are guaranteed to work. Note that the way the instantiation template
     * is generated is merely by stripping away the fields that are of
     * interface {@link Port}.
     *
     * @param args the instantiation arguments, as a <code>Map</code> from
     * names to argument values
     * @return the full object template
     * @exception PlasticException thrown if there is a problem
     * with the arguments (that couldn't be caught by typechecking) 
     */
    abstract protected Template getObjectTemplate1 (Map args)
	throws PlasticException;

    /**
     * Do the actual processing for {@link #make}. By the time this is
     * called, the argument map has already been typechecked against a
     * template returned from {@link #getObjectTemplate1}, so there will be
     * no missing arguments, no extra arguments, and raw casts that conform
     * to the template are guaranteed to work. Note that the arguments
     * handed to this method are guaranteed to never be touched again
     * by any outside force, as they will have been copied from the original
     * untrusted source.
     *
     * @param template the full object template as returned from a previous
     * call to {@link #getObjectTemplate1} on the given arguments
     * @param args the instantiation arguments, as a <code>Map</code> from
     * names to argument values
     * @return an appropriately-instantiated object
     * @exception PlasticException thrown if there is a problem
     * with the instantiation 
     */
    abstract protected Object make1 (Template template, Map args)
	throws PlasticException;

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Take a given template and return a new template which as added to it
     * the standard name and type for a single output.
     *
     * @param orig the original template
     * @return the new template
     */
    static public Template templateWithOut (Template orig)
    {
	return orig.withMore (new String[] { "out" },
			      new Class[] { DoublePort.class });
    }

    /**
     * Return the standard-form module constructor--<code>ClassName
     * (Factory, Template, Map)</code>--for the given class.
     *
     * @param moduleClass the class to query
     * @return the standard-form constructor of that class 
     */
    static public Constructor getStandardConstructor (Class moduleClass)
    {
	if (moduleClass == null)
	{
	    throw new IllegalArgumentException ("moduleClass = null");
	}

	try
	{
	    return moduleClass.getConstructor (CONSTRUCTOR_ARGS);
	}
	catch (NoSuchMethodException ex)
	{
	    throw new PlasticException ("Module class (" + 
					moduleClass.getName () + 
					") doesn't have a standard-form " +
					"public constructor.");
	}
    }
}
