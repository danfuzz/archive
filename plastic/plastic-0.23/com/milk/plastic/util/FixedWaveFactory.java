package com.milk.plastic.util;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Factory for fixed-input waveform modules. Such modules take no dynamic
 * inputs and produce a single waveform as output, based on fixed inputs of
 * given extrema and wavelength.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>v0: double</code></dt>
 * <dd>The first extremum of the output.</dd>
 * <dt><code>v1: double</code></dt>
 * <dd>The second extremum of the output.</dd>
 * <dt><code>wlen: double</code></dt>
 * <dd>The wavelength of the output.</dd>
 * <dt><code>phase: double</code></dt>
 * <dd>The phase offset of the output; <code>0</code> means no offset;
 * positive values cause a positive phase adjustment and negative values
 * cause a negative phase adjustment, with wraparound (equivalence to no
 * adjustment) at each integral value.</dd>
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
final public class FixedWaveFactory
extends BaseFactory
{
    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "v0", "v1", "wlen", "phase" },
		      new Class[] { Double.class, Double.class, 
                                    Double.class, Double.class });

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
    public FixedWaveFactory (String shortName, Class moduleClass)
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
    public FixedWaveFactory (String shortName, Class moduleClass,
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
