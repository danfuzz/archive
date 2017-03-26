package com.milk.plastic.modules.base;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for constant modules. Constant modules merely provide an
 * output port with a single given value.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>value: any</code></dt>
 * <dd>The value to provide on the output port. The port is made
 * to be the narrowest appropriate type (e.g., a double port if the
 * value is a double).</dd>
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
final public class ConstFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "Const";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "value" },
		      new Class[] { Object.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public ConstFactory ()
    {
	super (SHORT_NAME, TEMPLATE);
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected Template getObjectTemplate1 (Map args)
	throws PlasticException
    {
	Object value = args.get ("value");

	String[] name = new String[] { "out" };
	Class[] type = new Class[1];

	// should probably be more generic
	if (value instanceof Double)
	{
	    type[0] = DoublePort.class;
	}
	else
	{
	    throw new PlasticException ("Unable to create constant port " +
					"for type " + 
					value.getClass ().getName ());
	}

	return TEMPLATE.withMore (name, type);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	return new Const (this, template, args);
    }
}

