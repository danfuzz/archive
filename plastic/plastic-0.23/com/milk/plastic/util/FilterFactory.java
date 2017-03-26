package com.milk.plastic.util;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Factory for filter modules. Such modules take one dynamic input and
 * produce a single output, based on that input.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>in: double port</code></dt>
 * <dd>Port for the value to filter.</dd>
 * </dl>
 *
 * <p>The resulting modules always have a single output port named
 * <code>out</code>.</p>
 *
 * <p>When constructed, this class takes the class for the modules
 * to produce. Said class should have a "standard form" public constructor
 * of the form:</p>
 *
 * <blockquote><code>public <i>ClassName</i> (Factory factory, Template
 * template, Map args)</code></blockquote>
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
final public class FilterFactory
extends BaseFactory
{
    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "in", },
		      new Class[] { FieldRef.class });

    /** full template */
    private Template myFullTemplate;

    /** the constructor to use */
    private Constructor myConstructor;

    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param shortName non-null; the short name
     * @param moduleClass non-null; the class of the modules to produce
     */
    public FilterFactory (String shortName, Class moduleClass)
    {
	super (shortName, TEMPLATE);
	init (moduleClass);
    }

    /**
     * Construct an instance that optionally adds extra base arguments.
     *
     * @param shortName non-null; the short name
     * @param moduleClass non-null; the class of the modules to produce
     * @param names non-null; array of extra names to require 
     * @param types non-null; array of types corresponding to the names
     */
    public FilterFactory (String shortName, Class moduleClass,
			  String[] names, Class[] types)
    {
	super (shortName, TEMPLATE.withMore (names, types));
	init (moduleClass);
    }

    /**
     * Helper to initialize the instance.
     *
     * @param moduleClass non-null; the class of the modules to produce
     */
    private void init (Class moduleClass)
    {
	myConstructor = getStandardConstructor (moduleClass);
	myFullTemplate = templateWithOut (getFactoryTemplate ());
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected Template getObjectTemplate1 (Map args)
    {
	return myFullTemplate;
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
    {
	try
	{
	    Object[] constructArgs = new Object[] {this, template, args};
	    return myConstructor.newInstance (constructArgs);
	}
	catch (Exception ex)
	{
	    throw new PlasticException ("Trouble constructing module.",
					ex);
	}
    }
}
