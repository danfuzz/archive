package com.milk.plastic.modules.base;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for debug sinks. Debug sinks take an arbitrary number of
 * inputs and simply write out their values at each iteration.
 * As its name implies, this module exists primarily for debugging purposes.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>label: string</code></dt>
 * <dd>A label to print with each output.</dd>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of inputs to take. The inputs are named simply
 * <code>in_<i>n</i></code>, for <code><i>n</i></code> starting at
 * <code>0</code>.</dd>
 * </dl>
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
final public class DebugSinkFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "DebugSink";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "label", "count" },
		      new Class[] { String.class, Integer.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public DebugSinkFactory ()
    {
	super (SHORT_NAME, TEMPLATE);
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected Template getObjectTemplate1 (Map args)
	throws PlasticException
    {
	int count = ((Integer) args.get ("count")).intValue ();

	String[] names = new String[count];
	Class[] types = new Class[count];

	for (int i = 0; i < count; i++)
	{
	    names[i] = "in_" + i;
	    types[i] = FieldRef.class;
	}

	return TEMPLATE.withMore (names, types);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	return new DebugSink (this, template, args);
    }
}

