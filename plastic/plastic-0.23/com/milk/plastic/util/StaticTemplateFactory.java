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
 * Factory for modules whose templates don't vary from instance to
 * instance.
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
public class StaticTemplateFactory
extends BaseFactory
{
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
     * @param baseTemplate non-null; the base argument template
     * @param fullTemplate non-null; the full template
     */
    public StaticTemplateFactory (String shortName, Class moduleClass,
				  Template baseTemplate, Template fullTemplate)
    {
	super (shortName, baseTemplate);

	myConstructor = getStandardConstructor (moduleClass);

	if (fullTemplate == null)
	{
	    throw new IllegalArgumentException ("fullTemplate = null");
	}

	myFullTemplate = fullTemplate;
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected final Template getObjectTemplate1 (Map args)
    {
	return myFullTemplate;
    }

    // superclass's comment suffices
    protected final Object make1 (Template template, Map args)
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
