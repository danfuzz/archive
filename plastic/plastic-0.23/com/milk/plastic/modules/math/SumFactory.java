package com.milk.plastic.modules.math;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.modules.base.Null;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for summation modules. These take an arbitrary number of
 * inputs and add them together to produce a single output per
 * tick.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of inputs to take. The inputs are named simply
 * <code>in_<i>n</i></code>, for <code><i>n</i></code> starting at
 * <code>0</code>.</dd>
 * </dl>
 *
 * <p>The resulting modules always have a single output port named
 * <code>out</code>.</p>
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
final public class SumFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "Sum";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "count" },
		      new Class[] { Integer.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public SumFactory ()
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

	String[] names = new String[count + 1];
	Class[] types = new Class[count + 1];

	for (int i = 0; i < count; i++)
	{
	    names[i] = "in_" + i;
	    types[i] = FieldRef.class;
	}

	names[count] = "out";
	types[count] = DoublePort.class;

	return TEMPLATE.withMore (names, types);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	int count = ((Integer) args.get ("count")).intValue ();

	if (count < 1)
	{
	    throw new PlasticException ("Bad count (" + count + ")");
	}
	else if (count == 1)
	{
	    return new Null (this, template, args);
	}

	return new Sum (this, template, args);
    }
}
